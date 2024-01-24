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
    /** Constructed Dictionary data structure */
    dictionary: Dictionary
) {
    private def dictionaryEntryMapAsJson[A, B] (f1: (A, B) => Json) (map: Map[A, B]): Json =
        (map.map { case (key, value) => f1(key, value) }).toList.asJson

    private def dictionarySymbolSetAsJson[A] (f1: A => Json) (set: Set[A]): Json =
        (set.map(elem => f1(elem))).toList.asJson

    private def enumConstantAsJson(aNode: AstNode[Ast.DefEnumConstant]): Map[String, Json] = {
        val Value.EnumConstant(value, _) = analysis.valueMap(aNode.id)
        Map(value._1 -> value._2.asJson)
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

    def jsonWithOptional(key: String, optional: Option[Any], json: Json): Json = {
        optional match {
            case Some(value) => value match {
                case x: Format => Json.obj(key -> formatSpecifierAsJson(x)).deepMerge(json)
                case x: Int => Json.obj(key -> x.asJson).deepMerge(json)
                case Value.Struct(a, t) => {
                    val Value.AnonStruct(members) = a
                    val memberJson = members.map((key, value) => (key.toString -> valueAsJson(value))).asJson
                    Json.obj(key -> memberJson).deepMerge(json)
                }
                case x: Value => Json.obj(key -> valueAsJson(x)).deepMerge(json)
                case x: BigInt => Json.obj(key -> x.asJson).deepMerge(json)
                case x: Ast.QueueFull => Json.obj(key -> x.toString.asJson).deepMerge(json)
            }
            case None => json
        }
    }

    def formatSpecifierAsJson(format: Format): Json =
        format.fields.foldLeft(format.prefix) ((acc, inst) => acc + "{}" + inst._2).asJson

    def typeAsJson[T <: Type](elemType: T): Json = {
        elemType match {
            case Type.PrimitiveInt(kind) => {
                val kindString = kind.toString
                val size = if (kindString.startsWith("I")) kindString.split("I").tail else kindString.split("U").tail
                val signed = if (kindString.startsWith("I")) true else false
                Json.obj(
                    "name" -> kindString.asJson,
                    "kind" -> "integer".asJson,
                    "size" -> size.mkString("").toInt.asJson,
                    "signed" -> signed.asJson
                )
            }
            case Type.Float(kind) => {
                val kindString = kind.toString
                val size = kindString.split("F").tail
                Json.obj(
                    "name" -> kindString.asJson,
                    "kind" -> "float".asJson,
                    "size" -> size.mkString("").toInt.asJson
                )
            }
            case Type.String(size) => {
                val jsonObj = Json.obj(
                    "name" -> "string".asJson,
                    "kind" -> "string".asJson
                )
                size match {
                    case Some(s) => {
                        Json.obj("size" -> valueAsJson(analysis.valueMap(s.id))).deepMerge(jsonObj)
                    }
                    case None => jsonObj
                }
            }
            case Type.Array(node, _, _, _) => {
                Json.obj(
                    "name" -> analysis.getQualifiedName(Symbol.Array(node)).toString.asJson,
                    "kind" -> "qualifiedIdentifier".asJson,
                )
            }
            case Type.Enum(node, _, _) => {
                Json.obj(
                    "name" -> analysis.getQualifiedName(Symbol.Enum(node)).toString.asJson,
                    "kind" -> "qualifiedIdentifier".asJson,
                )
            }
            case Type.Struct(node, _, _, _, _) => {
                Json.obj(
                    "name" -> analysis.getQualifiedName(Symbol.Struct(node)).toString.asJson,
                    "kind" -> "qualifiedIdentifier".asJson,
                )
            }
            // TODO: things excluded from the spec - empty json / do not add
            case _ => {
                Json.obj(
                    "name" -> "not defined".asJson,
                    "kind" -> "not defined".asJson,
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

    def arrayElementsAsJson(elements: List[Value]): Json = {
        val arrayRes = for(e <- elements) yield {
            val res = e match {
                // Case where array is N-dimensional
                case Value.Array(a, t) => arrayElementsAsJson(a.elements)
                case _ => valueAsJson(e)
            }

            res.asJson
        }
        arrayRes.asJson
    }

    def valueAsJson[V <: Value](value: V): Json = {
        value match {
            case Value.PrimitiveInt(v, _) => v.asJson
            case Value.Integer(v) => v.asJson
            case Value.Float(v, _) => v.asJson
            case Value.Boolean(v) => v.asJson
            case Value.String(v) => v.asJson
            case Value.Array(a, t) => arrayElementsAsJson(a.elements)
            case Value.EnumConstant(v, _) => v._2.asJson
            case Value.Struct(Value.AnonStruct(members), t) => members.map((key, value) => (key.toString -> valueAsJson(value))).asJson
            case _ => value.toString.asJson
        }
    }

    private implicit def typeSymbolEncoder [T <: Symbol]: Encoder[T] = new Encoder[T] {
        override def apply(symbol: T): Json = {
            val qualifiedName = analysis.getQualifiedName(symbol).toString
            symbol match {
                case Symbol.Array(_, _, _) => {
                    val arrayType = analysis.typeMap(symbol.getNodeId)
                    val Type.Array(_, anonArray, default, format) = arrayType
                    val defaultJsonList: List[Json]= default match {
                        case Some(defaultVal) => for (elem <- defaultVal._1._1) yield valueAsJson(elem)
                        case None => List.empty[Json]
                    }

                    Json.obj(
                        "kind" -> "array".asJson,
                        "qualifiedName" -> qualifiedName.asJson,
                        "size" -> arrayType.getArraySize.asJson,
                        "elementType" -> typeAsJson(anonArray.eltType),
                        "default" -> defaultJsonList.asJson
                    )
                }
                case Symbol.Enum(_, node, _) => {
                    val Type.Enum(_, repType, default) = analysis.typeMap(symbol.getNodeId)
                    val enumDefault = default match {
                        case Some(defaultVal) => defaultVal.value._1
                        case None => ""
                    }
                    val identifiers = for (aNode <- node.data.constants) yield Map(aNode._2._1._1.toString -> valueAsJson(analysis.valueMap(aNode._2.id)))
                    Json.obj(
                        "kind" -> "enum".asJson,
                        "qualifiedName" -> qualifiedName.asJson,
                        "representationType" -> typeAsJson(repType),
                        "identifiers" -> identifiers.flatten.toMap.asJson,
                        "default" -> (qualifiedName ++ "." ++ enumDefault).asJson
                    )
                }
                case Symbol.Struct(_, node, _) => {
                    val Type.Struct(_, anonStruct, default, sizes, formats) = analysis.typeMap(symbol.getNodeId)
                    val Type.AnonStruct(members) = anonStruct
                    val membersFormatted = for(((key, t), index) <- members.zipWithIndex) yield {
                        val json = Json.obj("type" -> typeAsJson(t).asJson, "index" -> index.asJson)
                        val optionalMap = Map("size" -> sizes.get(key), "formatString" -> formats.get(key))
                        val withOptionals = optionalMap.foldLeft(json) ((acc, inst) => jsonWithOptional(inst._1, inst._2, acc))
                        (key.toString -> withOptionals)
                    }
                    val json = Json.obj(
                        "kind" -> "struct".asJson,
                        "qualifiedName" -> qualifiedName.asJson,
                        "members" -> membersFormatted.toMap.asJson,
                    )
                    jsonWithOptional("default", default, json)
                }
                case _ => Json.obj("TODO" -> "TODO".asJson)
            }
        }
    }

    private implicit def channelLimitEncoder: Encoder[TlmChannel.Limits] = new Encoder[TlmChannel.Limits] {
        override def apply(limits: TlmChannel.Limits): Json = {
            (limits.map { case (limitKind, (id, value)) => limitKind.toString -> valueAsJson(value)}).asJson
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
                    val ref = kind match {
                        case Ast.FormalParam.Ref => true
                        case Ast.FormalParam.Value => false
                    }
                    Json.obj(
                        "identifier" -> identifier.asJson,
                        "description" -> description.asJson, 
                        "type" -> typeAsJson(analysis.typeMap(typeNameNode.id)),
                        "ref" -> ref.asJson
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
                    val (commandKind, priority, queueFull) = kind match {
                        case Command.NonParam.Async(priority, queueFull) => ("async", priority, Some(queueFull))
                        case Command.NonParam.Guarded => ("guarded", None, None)
                        case Command.NonParam.Sync => ("sync", None, None)
                    }

                    val formalParams = data.params

                    val json = Json.obj(
                        "identifier" -> identifier.asJson,
                        "commandKind" -> commandKind.asJson, 
                        "opcode" -> opcode.asJson,
                        "description" -> description.asJson, 
                        "formalParams" -> formalParams.asJson
                    )

                    val optionalMap = Map("priority" -> priority, "queueFullBehavior" -> queueFull)
                    optionalMap.foldLeft(json) ((acc, inst) => jsonWithOptional(inst._1, inst._2, acc))
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
            val json = Json.obj(
                "identifier" -> node.data.name.asJson,
                "description" -> annotation.mkString("\n").asJson,
                "type" -> typeAsJson(param.paramType),
                "numericIdentifier" -> entry.identifier.asJson
            )
            jsonWithOptional("default", param.default, json)
        }
    }

    private implicit def eventEncoder: Encoder[EventEntry] = new Encoder[EventEntry] {
        override def apply(entry: EventEntry): Json = {
            val event = entry.event
            val (annotation, node, _) = event.aNode
            // val format = event.format.prefix // need to fix format so it adheres to spec (ie: python string format convention)
            val json = Json.obj(
                "identifier" -> event.getName.toString.asJson,
                "description" -> annotation.mkString("\n").asJson,
                "severity" -> node.data.severity.toString.asJson,
                "formalParams" -> node.data.params.asJson,
                "numericIdentifier" -> entry.identifier.asJson,
            )
            val optionalMap = Map("formatString" -> Some(event.format), "throttle" -> event.throttle)
            optionalMap.foldLeft(json) ((acc, inst) => jsonWithOptional(inst._1, inst._2, acc))
        }
    }

    private implicit def channelEncoder: Encoder[ChannelEntry] = new Encoder[ChannelEntry] {
        override def apply(entry: ChannelEntry): Json = {
            val channel = entry.channel
            val (annotation, node, _) = channel.aNode
            val json = Json.obj(
                "identifier" -> node.data.name.asJson,
                "description" -> annotation.mkString("\n").asJson,
                "type" -> typeAsJson(channel.channelType),
                "numericIdentifier" -> entry.identifier.asJson,
                "telemtryUpdate" -> channel.update.toString.asJson
                // "limit" -> Json.obj(
                //     "low" -> channel.lowLimits.asJson,
                //     "high" -> channel.highLimits.asJson
                // )
            )
            
            jsonWithOptional("formatString", channel.format, json)
        }
    }

     /** Given a set of symbols, splits set into subsets consisting of array, enum, and struct symbols */
    def splitTypeSymbolSet(symbolSet: Set[Symbol], outArray: Set[Symbol.Array], outEnum: Set[Symbol.Enum], outStruct: Set[Symbol.Struct]): (Set[Symbol.Array], Set[Symbol.Enum], Set[Symbol.Struct]) = {
        if (symbolSet.isEmpty) (outArray, outEnum, outStruct) else {
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