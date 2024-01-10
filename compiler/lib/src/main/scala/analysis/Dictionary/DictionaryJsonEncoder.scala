package fpp.compiler.analysis.dictionary

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.analysis._
import io.circe._
import io.circe.syntax._

case class CommandEntry(opcode: BigInt, command: Command)
case class ParamEntry(identifier: BigInt, param: Param)
case class EventEntry(identifier: BigInt, event: Event)
case class ChannelEntry(identifier: BigInt, channel: TlmChannel)

case class DictionaryJsonEncoder(
    /** Analysis data structure */
    analysis: Analysis,
    /** Constructed Dictionary data structure*/
    dictionary: Dictionary,
) {
    private def dictionaryEntryMapAsJson[A, B] (f1: (A, B) => Json) (map: Map[A, B]): Json =
        (map.map { case (key, value) => f1(key, value) }).toList.asJson

    private def dictionarySymbolSetAsJson[A] (f1: A => Json) (set: Set[A]): Json =
        (set.map(elem => f1(elem))).toList.asJson

    private def enumConstantAsJson(aNode: AstNode[Ast.DefEnumConstant]): Map[String, Json] = {
        val Value.EnumConstant(value, _) = analysis.valueMap(aNode.id)
        Map(value._1 -> value._2.asJson)
    }

    private def exprAsJson(expr: AstNode[Ast.Expr]): Option[Json] = {
        expr match {
            case AstNode(Ast.ExprLiteralInt(x), _) => Some(BigInt(x).asJson)
            case AstNode(Ast.ExprLiteralFloat(x), _) => Some(x.toDouble.asJson)
            case AstNode(Ast.ExprLiteralBool(x), _) => Some(x.toString.toBoolean.asJson)
            case AstNode(Ast.ExprLiteralString(x), _) => Some(x.toString.asJson)
            case AstNode(Ast.ExprIdent(x), _) => Some(x.toString.asJson)
            case AstNode(Ast.ExprArray(x), _) => {
                val arrayRes: List[Json] = for(elem <- x) yield exprAsJson(elem).asJson
                Some(arrayRes.asJson)
            }
            case _ => None // TODO: figure out how to get default value for type when none is specified
        }
    }
    private def structTypeMemberListToJson(members: List[Ast.Annotated[AstNode[Ast.StructTypeMember]]]): Json = {
        val defaultList = for(((_, memberNode, _), index) <- members.zipWithIndex) yield {
            val size: Option[Json] = memberNode.data.size match {
                case Some(sizeVal) => exprAsJson(sizeVal)
                case None => None
            }
            val jsonObj = Json.obj(
                "type" -> typeNameAsJson(Some(memberNode.data.typeName)),
                "index" -> index.asJson,
            )
            val formatString = memberNode.data.format

            val updatedJson = if (size.isDefined) jsonObj.deepMerge(Json.obj("size" -> size.asJson)) else jsonObj
            val updatedJson2 = if (formatString.isDefined) jsonObj.deepMerge(Json.obj("formatSpecifier" -> formatString.toString.asJson)) else updatedJson

            Map(memberNode.data.name -> updatedJson2)
        }
        defaultList.flatten.toMap.asJson
    }

    private def structDefaultMemberListToJson(members: List[AstNode[Ast.StructMember]]): Json = {
        val defaultList = for(memberNode <- members) yield {
            val v: Option[Json] = exprAsJson(memberNode.data.value) 
            if (v.isDefined) Map(memberNode.data.name -> v) else Map() // else case is when the member is not a valid dictionary entry and should be excluded
        }
        defaultList.flatten.toMap.asJson
    }

    private implicit def typeSymbolSetEncoder [T <: Symbol]: Encoder[Set[T]] = {
        def f1(symbol: T) = symbol.asJson
        Encoder.instance (dictionarySymbolSetAsJson (f1) _)
    }

    private implicit val commandMapEncoder: Encoder[Map[BigInt, Command]] = {
        def f1(opcode: BigInt, command: Command) = CommandEntry(opcode, command).asJson
        Encoder.instance (dictionaryEntryMapAsJson (f1) _)
    }

    private implicit val paramMapEncoder: Encoder[Map[BigInt, Param]] = {
        def f1(identifier: BigInt, param: Param) = ParamEntry(identifier, param).asJson
        Encoder.instance (dictionaryEntryMapAsJson (f1) _)
    }

    private implicit val eventMapEncoder: Encoder[Map[BigInt, Event]] = {
        def f1(identifier: BigInt, event: Event) = EventEntry(identifier, event).asJson
        Encoder.instance (dictionaryEntryMapAsJson (f1) _)
    }

    private implicit val channelMapEncoder: Encoder[Map[BigInt, TlmChannel]] = {
        def f1(identifier: BigInt, event: TlmChannel) = ChannelEntry(identifier, event).asJson
        Encoder.instance (dictionaryEntryMapAsJson (f1) _)
    }

    def typeAsJson[T <: Type](elemType: T): Json = {
        elemType match {
            case Type.PrimitiveInt(kind) => {
                val stringVal = kind.toString
                val size = if (stringVal.startsWith("I")) stringVal.split("I").tail else stringVal.split("U").tail
                val signed = if (stringVal.startsWith("I")) true else false
                Json.obj(
                    "name" -> stringVal.asJson,
                    "kind" -> "integer".asJson,
                    "size" -> size.mkString("").toInt.asJson,
                    "signed" -> signed.asJson
                )
            }
            case Type.Float(kind) => {
                val stringVal = kind.toString
                val size = stringVal.split("F").tail
                Json.obj(
                    "name" -> stringVal.asJson,
                    "kind" -> "float".asJson,
                    "size" -> size.mkString("").toInt.asJson
                )
            }
            case _ => {
                Json.obj(
                    "name" -> "stringVal".asJson,
                    "kind" -> "not defined".asJson,
                    "size" -> "".asJson
                )
            }
        }
    }

    def typeNameAsJson(typeNameNode: Option[AstNode[Ast.TypeName]]): Json = {
        typeNameNode match {
            case Some(node) => {
                node match {
                    case AstNode(Ast.TypeNameInt(value), _) => {
                        val stringVal = value.toString
                        val size = if (stringVal.startsWith("I")) stringVal.split("I").tail else stringVal.split("U").tail
                        val signed = if (stringVal.startsWith("I")) true else false
                        Json.obj(
                            "name" -> stringVal.asJson,
                            "kind" -> "integer".asJson,
                            "size" -> size.mkString("").toInt.asJson,
                            "signed" -> signed.asJson
                        )
                    }
                    case AstNode(Ast.TypeNameFloat(value), _) => {
                        val stringVal = value.toString
                        val size = stringVal.split("F").tail
                        Json.obj(
                            "name" -> stringVal.asJson,
                            "kind" -> "float".asJson,
                            "size" -> size.mkString("").toInt.asJson
                        )
                    }
                    case AstNode(Ast.TypeNameBool, _) => Json.obj("name" -> "bool".asJson, "kind" -> "bool".asJson)
                     case AstNode(Ast.TypeNameString(value), _) => {
                        val size: Option[Int] = value match {
                            case Some(AstNode(Ast.ExprLiteralInt(sizeVal), _)) => Some(sizeVal.toInt)
                            case _ => None
                        }
                        val jsonObj = Json.obj(
                            "name" -> "string".asJson,
                            "kind" -> "string".asJson
                        )
                        
                        if (size.isDefined) jsonObj.deepMerge(Json.obj("size" -> size.asJson)) else jsonObj
                    }
                    case AstNode(Ast.TypeNameQualIdent(AstNode(qualIdent, _)), _) => {
                        Json.obj(
                            "name" -> qualIdent.toIdentList.mkString(".").asJson,
                            "kind" -> "qualifiedIdentifier".asJson
                        ) 
                    }
                    case _ => {
                        // exclude any other type names that do not match the above
                        Json.obj("" -> "".asJson)
                    }
                }
            }
            case None => Json.obj("" -> "".asJson)
        }
    }

    private implicit def typeSymbolEncoder [T <: Symbol]: Encoder[T] = new Encoder[T] {
        override def apply(symbol: T): Json = {
            val qualifiedName = analysis.getQualifiedName(symbol).toString
            symbol match {
                case Symbol.Array(_, node, _) => {
                    val arrayType = analysis.typeMap(symbol.getNodeId)
                    val Type.Array(_, _, default, format) = arrayType
                    val arrayDefault = default
                    println(arrayDefault)
                    Json.obj(
                        "kind" -> "array".asJson,
                        "qualifiedName" -> qualifiedName.asJson,
                        "size" -> arrayType.getArraySize.asJson,
                        "elementType" -> typeNameAsJson(Some(node.data.eltType)),
                        "default" -> arrayDefault.toString.asJson
                    )
                }
                case Symbol.Enum(_, node, _) => {
                    val Type.Enum(_, repType, default) = analysis.typeMap(symbol.getNodeId)
                    val enumDefault = default match {
                        case Some(defaultVal) => defaultVal.value._1
                        case None => ""
                    }
                    val identifiers = for (aNode <- node.data.constants) yield enumConstantAsJson(aNode._2)
                    Json.obj(
                        "kind" -> "enum".asJson,
                        "qualifiedName" -> qualifiedName.asJson,
                        "representationType" -> typeAsJson(repType), // note: type name info does not appear for enums that do not have types explicitely stated
                        "identifiers" -> identifiers.flatten.toMap.asJson,
                        "default" -> (qualifiedName ++ "." ++ enumDefault).asJson
                    )
                }
                case Symbol.Struct(_, node, _) => {
                    val default: Json = node.data.default match {
                        case Some(AstNode(Ast.ExprIdent(value), _)) => "".asJson
                        case Some(AstNode(Ast.ExprStruct(value), _)) => structDefaultMemberListToJson(value)
                        case Some(_) => "".asJson
                        case None => "".asJson
                    }
                    Json.obj(
                        "kind" -> "struct".asJson,
                        "qualifiedName" -> node.data.name.asJson,
                        "members" -> structTypeMemberListToJson(node.data.members),
                        "default" -> default
                    )
                }
                case _ => Json.obj("TODO" -> "TODO".asJson)
            }
        }
    }

    private implicit def channelLimitEncoder: Encoder[TlmChannel.Limits] = new Encoder[TlmChannel.Limits] {
        override def apply(limits: TlmChannel.Limits): Json = {
            (limits.map { case (limitKind, valueNode) => limitKind.toString -> valueNode._2.toString }).asJson
        }
    }

    private implicit def formalParamListEncoder: Encoder[Ast.FormalParamList] = new Encoder[Ast.FormalParamList] {
        override def apply(params: Ast.FormalParamList): Json = {
            val paramListJson = params.length match {
                case 0 => List(List("").asJson)
                case _ => for (paramEntry <- params) yield {
                    val (_, elem, annotation) = paramEntry
                    val description = annotation.mkString("\n")
                    val AstNode(Ast.FormalParam(kind, identifier, typeNameNode), _) = elem
                    // TODO: figure out how to encode optional fields (ie: size). Currently size is "null" if its None
                    Json.obj(
                        "identifier" -> identifier.asJson,
                        "description" -> description.asJson, 
                        "type" -> typeNameAsJson(Some(typeNameNode)),
                        "ref" -> false.asJson
                    )
                }
            }
            paramListJson.asJson
        }
    }

    private implicit def commandEncoder: Encoder[CommandEntry] = new Encoder[CommandEntry] {
        override def apply(entry: CommandEntry): Json = {
            val opcode = entry.opcode
            val command  = entry.command
            val identifier = command.getName.toString // TODO: need to use qualified name (ie: module.component.commandName)
            command match {
                case Command.NonParam(aNode, kind) => {
                    val (annotation, node, _) = aNode
                    val data = node.data
                    val description = annotation.mkString("\n")
                    // kind can either be: async, guarded, or sync
                    val commandKind = kind match {
                        case _: Command.NonParam.Async => "async"
                        case Command.NonParam.Guarded => "guarded"
                        case Command.NonParam.Sync => "sync"
                    }
                    val priority = data.priority match {
                        case Some(x) => exprAsJson(x)
                        case None => None
                    }
                    val queueFullBehavior: Option[String] = data.queueFull match {
                        case Some(_) => Some(Analysis.getQueueFull(data.queueFull.map(_.data)).toString)
                        case None => None
                    }

                    val formalParams = data.params
                    // val newCommand = Command(identifier, commandKind, opcode, description, formalParams, priority, queueFullBehavior)
                    val initJson = Json.obj(
                        "identifier" -> identifier.asJson,
                        "commandKind" -> commandKind.asJson, 
                        "opcode" -> opcode.asJson,
                        "description" -> description.asJson, 
                        "formalParams" -> formalParams.asJson,
                    )

                    val updatedJson = if(priority.isDefined) Json.obj("priority" -> priority.asJson).deepMerge(initJson) else initJson
                    val updatedJson2 = if(queueFullBehavior.isDefined) Json.obj("queueFullBehavior" -> queueFullBehavior.asJson).deepMerge(updatedJson) else updatedJson
                    updatedJson2
                }
                // case where command is param set/save command
                case fpp.compiler.analysis.Command.Param(aNode, kind) => {
                    val (annotation, node, _) = aNode
                    val data = node.data
                    val commandKind = kind match {
                        case Command.Param.Set => "set"
                        case Command.Param.Save => "save"
                    }
                    Json.obj(
                        "identifier" -> identifier.asJson,
                        "commandKind" -> commandKind.asJson, 
                        "opcode" -> opcode.asJson,
                        "description" -> annotation.mkString("\n").asJson, 
                        "formalParams" -> List.empty[String].asJson,
                    )
                }
            }
        }
    }

    private implicit def paramEncoder: Encoder[ParamEntry] = new Encoder[ParamEntry] {
        override def apply(entry: ParamEntry): Json = {
            val param = entry.param
            val (annotation, node, _) = param.aNode
            val identifier = node.data.name
            val description = annotation.mkString("\n")
            // TODO: get default values
           
            Json.obj(
                "identifier" -> identifier.asJson,
                "description" -> description.asJson,
                // "default" -> default.asJson,
                "type" -> typeNameAsJson(Some(node.data.typeName)),
                "numericIdentifier" -> entry.identifier.asJson
            )
        }
    }

    private implicit def eventEncoder: Encoder[EventEntry] = new Encoder[EventEntry] {
        override def apply(entry: EventEntry): Json = {
            val event = entry.event
            val identifier = event.getName.toString
            val (annotation, node, _) = event.aNode
            // val format = event.format.prefix // need to fix format so it adheres to spec (ie: python string format convention)
            Json.obj(
                "identifier" -> identifier.asJson,
                "description" -> annotation.mkString("\n").asJson,
                "severity" -> node.data.severity.toString.asJson,
                "formalParams" -> node.data.params.asJson,
                "numericIdentifier" -> entry.identifier.asJson,
                // "formatString" -> format.asJson,
                "throttle" -> event.throttle.asJson
            )
        }
    }

    private implicit def channelEncoder: Encoder[ChannelEntry] = new Encoder[ChannelEntry] {
        override def apply(entry: ChannelEntry): Json = {
            val channel = entry.channel
            val (annotation, node, _) = channel.aNode
            val identifier = node.data.name
            // val format = channel.format.prefix // need to fix format so it adheres to spec (ie: python string format convention)
            Json.obj(
                "identifier" -> identifier.asJson,
                "description" -> annotation.mkString("\n").asJson,
                "type" -> typeNameAsJson(Some(node.data.typeName)),
                "numericIdentifier" -> entry.identifier.asJson,
                "telemtryUpdate" -> channel.update.toString.asJson,
                "limit" -> Json.obj(
                    "low" -> channel.lowLimits.asJson,
                    "high" -> channel.highLimits.asJson
                )
                // "formatString" -> format.asJson
            )
        }
    }

     /** Given a set of symbols, splits set into subsets consisting of array, enum, and struct symbols */
    def splitTypeSymbolSet(symbolSet: Set[Symbol], outArray: Set[Symbol.Array], outEnum: Set[Symbol.Enum], outStruct: Set[Symbol.Struct]): (Set[Symbol.Array], Set[Symbol.Enum], Set[Symbol.Struct]) = {
        if (symbolSet.tail.isEmpty) (outArray, outEnum, outStruct) else {
            val (tail, outA, outE, outS) = symbolSet.head match {
                case h: Symbol.Array => (symbolSet.tail, outArray + h, outEnum, outStruct)
                case h: Symbol.Enum => (symbolSet.tail, outArray, outEnum + h, outStruct)
                case h: Symbol.Struct => (symbolSet.tail, outArray, outEnum, outStruct + h)
                case _ => (symbolSet.tail, outArray, outEnum, outStruct)
            }
            splitTypeSymbolSet(tail, outA, outE, outS)
        }
    }

    def dictionaryToJson: Json = {
        // split set into individual sets consisting of each symbol type (arrays, enums, structs)
        val (arraySymbolSet, enumSymbolSet, structSymbolSet) = splitTypeSymbolSet(dictionary.typeSymbolSet, Set(), Set(), Set())

        Json.obj(
            "arrays" -> arraySymbolSet.asJson,
            "enums" -> enumSymbolSet.asJson,
            "structs" -> structSymbolSet.asJson,
            "commands" -> dictionary.commandEntryMap.asJson,
            "parameters" -> dictionary.paramEntryMap.asJson,
            "events" -> dictionary.eventEntryMap.asJson,
            "telemtryChannels" -> dictionary.channelEntryMap.asJson
        )
    }
}