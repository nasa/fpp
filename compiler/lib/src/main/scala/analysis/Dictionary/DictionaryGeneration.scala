package fpp.compiler.analysis.dictionary

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.analysis._
import io.circe._
import io.circe.syntax._

case class DictionaryJsonEncoder(
    /** Analysis data structure */
    analysis: Analysis,
    /** Constructed Dictionary data structure*/
    dictionary: Dictionary,
) {
    case class CommandEntry(opcode: BigInt, command: Command)
    case class ParamEntry(identifier: BigInt, param: Param)
    case class EventEntry(identifier: BigInt, event: Event)
    case class ChannelEntry(identifier: BigInt, channel: TlmChannel)

    private def mapAsJsonList[A, B] (f1: (A, B) => Json) (map: Map[A, B]): Json =
        (map.map { case (key, value) => f1(key, value) }).toList.asJson

    private def setAsJson[A] (f1: A => Json) (set: Set[A]): Json =
        (set.map(elem => f1(elem))).toList.asJson

    // question: where are the default values calculated for the enum constants that do not have values specified?
    private def enumConstantListToJson(constants: List[Ast.Annotated[AstNode[Ast.DefEnumConstant]]]): Json = {
        val enumList = for(((_, enumNode, _), count) <- constants.zipWithIndex) yield {
            val value: BigInt = enumNode.data.value match {
                case Some(AstNode(Ast.ExprLiteralInt(x), _)) => BigInt(x)
                case Some(_) => count
                case None => count
            }
            Map(enumNode.data.name -> value)
        }
        enumList.flatten.toMap.asJson
    }

    private def structDefaultMemberListToJson(members: List[AstNode[Ast.StructMember]]): Json = {
        val defaultList = for(memberNode <- members) yield {
            val v: BigInt = memberNode.data.value match {
                case AstNode(Ast.ExprLiteralInt(x), _) => BigInt(x)
                case _ => 0 // TODO: figure out how to get default value for type when none is specified
            }
            Map(memberNode.data.name -> v)
        }
        defaultList.flatten.toMap.asJson
    }

    private implicit def typeSymbolSetEncoder [T <: Symbol]: Encoder[Set[T]] = {
        def f1(symbol: T) = symbol.asJson
        Encoder.instance (setAsJson (f1) _)
    }

    private implicit val commandMapEncoder: Encoder[Map[BigInt, Command]] = {
        def f1(opcode: BigInt, command: Command) = CommandEntry(opcode, command).asJson
        Encoder.instance (mapAsJsonList (f1) _)
    }

    private implicit val paramMapEncoder: Encoder[Map[BigInt, Param]] = {
        def f1(identifier: BigInt, param: Param) = ParamEntry(identifier, param).asJson
        Encoder.instance (mapAsJsonList (f1) _)
    }

    private implicit val eventMapEncoder: Encoder[Map[BigInt, Event]] = {
        def f1(identifier: BigInt, event: Event) = EventEntry(identifier, event).asJson
        Encoder.instance (mapAsJsonList (f1) _)
    }

    private implicit val channelMapEncoder: Encoder[Map[BigInt, TlmChannel]] = {
        def f1(identifier: BigInt, event: TlmChannel) = ChannelEntry(identifier, event).asJson
        Encoder.instance (mapAsJsonList (f1) _)
    }


    def extractTypeNameFromNode(typeNameNode: Option[AstNode[Ast.TypeName]]): (String, String, Option[BigInt]) = {
        typeNameNode match {
            case Some(node) => {
                node match {
                    case AstNode(Ast.TypeNameString(value), _) => {
                        val stringSize = value match {
                            case Some(AstNode(Ast.ExprLiteralInt(sizeVal), _)) => Some(BigInt(sizeVal))
                            case None => None
                            case _ => None
                        }
                        ("string", "string", stringSize)
                    }
                    case AstNode(Ast.TypeNameInt(value), _) => {
                        val stringVal = value.toString
                        val intSize = if (stringVal.startsWith("I")) stringVal.split("I").tail else stringVal.split("U").tail
                        (stringVal, "integer", Some(BigInt(intSize.mkString(""))))
                    }
                    case AstNode(Ast.TypeNameFloat(value), _) => {
                        val stringVal = value.toString
                        val floatSize = stringVal.split("F").tail
                        (stringVal, "float", Some(BigInt(floatSize.mkString(""))))
                    }
                    case AstNode(Ast.TypeNameBool, _) => ("bool", "bool", None)
                    case AstNode(Ast.TypeNameQualIdent(AstNode(qualIdent, _)), _) => (qualIdent.toIdentList.mkString("."), "qualifiedIdentifier", None)
                    case _ => {
                        // exclude any other type names that do not match the above
                        ("not implemented", "not implemented", None)
                    }
                }
            }
            case None => ("not implemented", "not implemented", None)
        }
    }

    private implicit def typeSymbolEncoder [T <: Symbol]: Encoder[T] = new Encoder[T] {
        override def apply(symbol: T): Json = {
            symbol match {
                case Symbol.Array(_, node, _) => {
                    val (typeName, typeKind, size) = extractTypeNameFromNode(Some(node.data.eltType))
                    Json.obj(
                        "kind" -> "array".asJson,
                        "qualifiedName" -> node.data.name.asJson,
                        "size" -> "".asJson,
                        "elementType" -> Json.obj(
                            "name" -> typeName.asJson,
                            "kind" -> typeKind.asJson,
                            "size" -> size.asJson
                        ),
                        "default" -> List.empty[String].asJson
                    )
                }
                case Symbol.Enum(_, node, _) => {
                    val (typeName, typeKind, size) = extractTypeNameFromNode(node.data.typeName)
                    val default: String = node.data.default match {
                        case Some(AstNode(Ast.ExprIdent(value), _)) => value.toString
                        case Some(_) => ""
                        case None => ""
                    }
                    Json.obj(
                        "kind" -> "enum".asJson,
                        "qualifiedName" -> node.data.name.asJson,
                        "representationType" -> Json.obj(
                            "name" -> typeName.asJson,
                            "kind" -> typeKind.asJson,
                            "size" -> size.asJson
                        ),
                        "identifiers" -> enumConstantListToJson(node.data.constants),
                        "default" -> default.asJson
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
                    val (typeName, typeKind, size) = extractTypeNameFromNode(Some(typeNameNode))
                    // TODO: figure out how to encode optional fields (ie: size). Currently size is "null" if its None
                    Json.obj(
                        "identifier" -> identifier.asJson,
                        "description" -> description.asJson, 
                        "type" -> Json.obj(
                            "name" -> typeName.asJson,
                            "kind" -> typeKind.asJson,
                            "size" -> size.asJson
                        ),
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
                case fpp.compiler.analysis.Command.NonParam(aNode, kind) => {
                    val (annotation, node, _) = aNode
                    val data = node.data
                    val description = annotation.mkString("\n")
                    // kind can either be: async, guarded, or sync
                    val commandKind = kind match {
                        case _: fpp.compiler.analysis.Command.NonParam.Async => "async"
                        case fpp.compiler.analysis.Command.NonParam.Guarded => "guarded"
                        case fpp.compiler.analysis.Command.NonParam.Sync => "sync"
                    }
                    // will "priority" will always be type ExprLiteralInt?
                    val priority = data.priority match {
                        case Some(AstNode(Ast.ExprLiteralInt(value), _)) => Some(BigInt(value))
                        case _ => None
                    }
                    val queueFullBehavior = Some(fpp.compiler.analysis.Analysis.getQueueFull(data.queueFull.map(_.data)).toString)
                    val formalParams = data.params
                    // val newCommand = Command(identifier, commandKind, opcode, description, formalParams, priority, queueFullBehavior)
                    Json.obj(
                        "identifier" -> identifier.asJson,
                        "commandKind" -> commandKind.asJson, 
                        "opcode" -> opcode.asJson,
                        "description" -> description.asJson, 
                        "formalParams" -> formalParams.asJson,
                        "priority" -> 0.asJson,
                        "queueFullBehavior" -> "".asJson
                    )
                }
                // case where command is param set/save command
                case fpp.compiler.analysis.Command.Param(aNode, kind) => {
                    val (annotation, node, _) = aNode
                    val data = node.data
                    val commandKind = kind match {
                        case fpp.compiler.analysis.Command.Param.Set => "set"
                        case fpp.compiler.analysis.Command.Param.Save => "save"
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
            val (typeName, typeKind, size) = extractTypeNameFromNode(Some(node.data.typeName))
            // TODO: get default values
           
            Json.obj(
                "identifier" -> identifier.asJson,
                "description" -> description.asJson,
                // "default" -> default.asJson,
                "type" -> Json.obj(
                    "name" -> typeName.asJson,
                    "kind" -> typeKind.asJson,
                    "size" -> size.asJson
                ),
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
            val (typeName, typeKind, size) = extractTypeNameFromNode(Some(node.data.typeName))
            // val format = channel.format.prefix // need to fix format so it adheres to spec (ie: python string format convention)
            Json.obj(
                "identifier" -> identifier.asJson,
                "description" -> annotation.mkString("\n").asJson,
                "type" -> Json.obj(
                    "type" -> typeName.asJson,
                    "kind" -> typeKind.asJson,
                    "size" -> size.asJson
                ),
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

    def dictionaryToJson(): Json = {
        val commandJson = dictionary.commandEntryMap.asJson
        val paramJson = dictionary.paramEntryMap.asJson
        val eventJson = dictionary.eventEntryMap.asJson
        val channelJson = dictionary.channelEntryMap.asJson
        val arraySymbolJson = dictionary.arraySymbolSet.asJson
        val enumSymbolJson = dictionary.enumSymbolSet.asJson
        val structSymbolJson = dictionary.structSymbolSet.asJson
        Json.obj(
            "arrays" -> arraySymbolJson,
            "enums" -> enumSymbolJson,
            "structs" -> structSymbolJson,
            "commands" -> commandJson,
            "parameters" -> paramJson,
            "events" -> eventJson,
            "telemtryChannels" -> channelJson
        )
    }
}