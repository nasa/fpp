package fpp.compiler.analysis.dictionary

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.analysis._
import io.circe._
import io.circe.syntax._

case class DictionaryGeneration() {
    case class CommandEntry(opcode: BigInt, command: Command)
    case class ParamEntry(identifier: BigInt, param: Param)
    case class EventEntry(identifier: BigInt, event: Event)
    case class ChannelEntry(identifier: BigInt, channel: TlmChannel)

    private def mapAsJsonList[A, B] (f1: (A, B) => Json) (map: Map[A, B]): Json =
        (map.map { case (key, value) => f1(key, value) }).toList.asJson

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

    def extractTypeNameFromNode(typeNameNode: AstNode[Ast.TypeName]): (String, String, Option[BigInt]) = {
        return typeNameNode match {
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

    private implicit def channelLimitEncoder: Encoder[TlmChannel.Limits] = new Encoder[TlmChannel.Limits] {
        override def apply(limits: TlmChannel.Limits): Json = {
            (limits.map { case (limitKind, valueNode) => limitKind.toString -> valueNode._2.toString }).asJson
        }
    }

    private implicit def formalParamListEncoder: Encoder[Ast.FormalParamList] = new Encoder[Ast.FormalParamList] {
        override def apply(params: Ast.FormalParamList): Json = {
            val paramListJson = params.length match {
                case 0 => return List(List("").asJson).asJson
                case _ => for (paramEntry <- params) yield {
                    val (_, elem, annotation) = paramEntry
                    val description = annotation.mkString("\n")
                    val AstNode(Ast.FormalParam(kind, identifier, typeNameNode), _) = elem
                    val (typeName, typeKind, size) = extractTypeNameFromNode(typeNameNode)
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
            command match {
                case fpp.compiler.analysis.Command.NonParam(aNode, kind) => {
                    val (annotation, node, _) = aNode
                    val data = node.data
                    val identifier = data.name.toString // TODO: need to use qualified name (ie: module.component.commandName)
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
                    println("TODO")
                    Json.obj(
                        "identifier" -> "".asJson,
                        "commandKind" -> "".asJson, 
                        "opcode" -> 0.asJson,
                        "description" -> "".asJson, 
                        "formalParams" -> List("something").asJson,
                        "priority" -> 0.asJson,
                        "queueFullBehavior" -> "".asJson
                    )
                }
            }
        }
    }

    private implicit def paramEncoder: Encoder[ParamEntry] = new Encoder[ParamEntry] {
        override def apply(entry: ParamEntry): Json = {
            val identifier = entry.identifier
            val param = entry.param
            val (annotation, node, _) = param.aNode
            val description = annotation.mkString("\n")
            val (typeName, typeKind, size) = extractTypeNameFromNode(node.data.typeName)
            val numId = node.data.id match {
                case Some(AstNode(Ast.ExprLiteralInt(value), _)) => Some(BigInt(value))
                case _ => None
            }
            // TODO: get default values
           
            val setOpcode = param.setOpcode
            val saveOpcode = param.saveOpcode
            Json.obj(
                "identifier" -> identifier.asJson,
                "description" -> description.asJson,
                // "default" -> default.asJson,
                "type" -> Json.obj(
                    "name" -> typeName.asJson,
                    "kind" -> typeKind.asJson,
                    "size" -> size.asJson
                ),
                "numericIdentifier" -> numId.asJson, 
                "setOpcode" -> setOpcode.asJson,
                "saveOpcode" -> saveOpcode.asJson,
            )
        }
    }

    private implicit def eventEncoder: Encoder[EventEntry] = new Encoder[EventEntry] {
        override def apply(entry: EventEntry): Json = {
            val identifier = entry.identifier
            val event = entry.event
            val (annotation, node, _) = event.aNode
            val numId = node.data.id match {
                case Some(AstNode(Ast.ExprLiteralInt(value), _)) => Some(BigInt(value))
                case _ => None
            }
            // val format = event.format.prefix // need to fix format so it adheres to spec (ie: python string format convention)
            Json.obj(
                "identifier" -> identifier.asJson,
                "description" -> annotation.mkString("\n").asJson,
                "severity" -> node.data.severity.toString.asJson,
                "formalParams" -> node.data.params.asJson,
                "numericIdentifier" -> numId.asJson,
                // "formatString" -> format.asJson,
                "throttle" -> event.throttle.asJson
            )
        }
    }

    private implicit def channelEncoder: Encoder[ChannelEntry] = new Encoder[ChannelEntry] {
        override def apply(entry: ChannelEntry): Json = {
            val identifier = entry.identifier
            val channel = entry.channel
            val (annotation, node, _) = channel.aNode
            val numId = node.data.id match {
                case Some(AstNode(Ast.ExprLiteralInt(value), _)) => Some(BigInt(value))
                case _ => None
            }
            val (typeName, typeKind, size) = extractTypeNameFromNode(node.data.typeName)
            // val format = channel.format.prefix // need to fix format so it adheres to spec (ie: python string format convention)
            Json.obj(
                "identifier" -> identifier.asJson,
                "description" -> annotation.mkString("\n").asJson,
                "type" -> Json.obj(
                    "type" -> typeName.asJson,
                    "kind" -> typeKind.asJson,
                    "size" -> size.asJson
                ),
                "numericIdentifier" -> numId.asJson,
                "telemtryUpdate" -> channel.update.toString.asJson,
                "limit" -> Json.obj(
                    "low" -> channel.lowLimits.asJson,
                    "high" -> channel.highLimits.asJson
                )
                // "formatString" -> format.asJson
            )
        }
    }

    def dictionaryToJson(d: dictionary.Dictionary): Json = {
        // val jsonRes1 = d.resolvedIdCommandMap.asJson
        // val jsonRes2 = d.resolvedIdParamMap.asJson
        // val jsonRes3 = d.resolvedIdEventMap.asJson
        val jsonRes4 = d.resolvedIdChannelMap.asJson
        jsonRes4
    }
}