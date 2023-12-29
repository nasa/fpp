package fpp.compiler.analysis.dictionary

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.analysis._
import fpp.compiler.analysis.dictionary._
import io.circe._
import io.circe.syntax._

case class DictionaryGeneration() {
    private def mapAsJsonList[A, B] (f1: (A, B) => Json) (map: Map[A, B]): Json =
        (map.map { case (key, value) => f1(key, value) }).toList.asJson

    private implicit val commandMapEncoder: Encoder[Map[BigInt, CommandEntry]] = {
        def f1(opcode: BigInt, command: CommandEntry) = command.asJson
        Encoder.instance (mapAsJsonList (f1) _)
    }

    private implicit val paramMapEncoder: Encoder[Map[BigInt, ParamEntry]] = {
        def f1(identifier: BigInt, param: ParamEntry) = param.asJson
        Encoder.instance (mapAsJsonList (f1) _)
    }

    private implicit val eventMapEncoder: Encoder[Map[BigInt, EventEntry]] = {
        def f1(identifier: BigInt, event: EventEntry) = event.asJson
        Encoder.instance (mapAsJsonList (f1) _)
    }

    private implicit val channelMapEncoder: Encoder[Map[BigInt, TlmChannelEntry]] = {
        def f1(identifier: BigInt, channel: TlmChannelEntry) = channel.asJson
        Encoder.instance (mapAsJsonList (f1) _)
    }

    def extractTypeNameFromNode(typeNameNode: AstNode[Ast.TypeName]): (String, String, Option[BigInt]) = {
        typeNameNode match {
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
                case 0 => List(List.empty[String].asJson)
                case _ => for (paramEntry <- params) yield {
                    val (_, elem, annotation) = paramEntry
                    val AstNode(Ast.FormalParam(kind, identifier, typeNameNode), _) = elem
                    val (typeName, typeKind, size) = extractTypeNameFromNode(typeNameNode)
                    // TODO: figure out how to encode optional fields (ie: size). Currently size is "null" if its None
                    Json.obj(
                        "identifier" -> identifier.asJson,
                        "description" -> annotation.mkString("\n").asJson, 
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
            val opcode = entry.resolvedIdentifier
            val command  = entry.command
            command match {
                case fpp.compiler.analysis.Command.NonParam(aNode, kind) => {
                    val (annotation, node, _) = aNode
                    val data = node.data
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
                    // val newCommand = Command(identifier, commandKind, opcode, description, formalParams, priority, queueFullBehavior)
                    Json.obj(
                        "identifier" -> entry.fullyQualifiedName.asJson,
                        "commandKind" -> commandKind.asJson, 
                        "opcode" -> opcode.asJson,
                        "description" -> annotation.mkString("\n").asJson, 
                        "formalParams" -> data.params.asJson,
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
                        "identifier" -> entry.fullyQualifiedName.asJson,
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
            val (typeName, typeKind, size) = extractTypeNameFromNode(node.data.typeName)
            // TODO: get default values
            Json.obj(
                "identifier" -> entry.fullyQualifiedName.asJson,
                "description" -> annotation.mkString("\n").asJson,
                // "default" -> default.asJson,
                "type" -> Json.obj(
                    "name" -> typeName.asJson,
                    "kind" -> typeKind.asJson,
                    "size" -> size.asJson
                ),
                "numericIdentifier" -> entry.resolvedIdentifier.asJson, 
                "setOpcode" -> entry.resolvedSetIdentifier.asJson,
                "saveOpcode" -> entry.resolvedSaveIdentifier.asJson,
            )
        }
    }

    private implicit def eventEncoder: Encoder[EventEntry] = new Encoder[EventEntry] {
        override def apply(entry: EventEntry): Json = {
            val event = entry.event
            val (annotation, node, _) = event.aNode
            // val format = event.format.prefix // need to fix format so it adheres to spec (ie: python string format convention)
            Json.obj(
                "identifier" -> entry.fullyQualifiedName.asJson,
                "description" -> annotation.mkString("\n").asJson,
                "severity" -> node.data.severity.toString.asJson,
                "formalParams" -> node.data.params.asJson,
                "numericIdentifier" -> entry.resolvedIdentifier.asJson,
                // "formatString" -> format.asJson,
                "throttle" -> event.throttle.asJson
            )
        }
    }

    private implicit def channelEncoder: Encoder[TlmChannelEntry] = new Encoder[TlmChannelEntry] {
        override def apply(entry: TlmChannelEntry): Json = {
            val channel = entry.channel
            val (annotation, node, _) = channel.aNode
            val (typeName, typeKind, size) = extractTypeNameFromNode(node.data.typeName)
            // val format = channel.format.prefix // need to fix format so it adheres to spec (ie: python string format convention)
            Json.obj(
                "identifier" -> entry.fullyQualifiedName.asJson,
                "description" -> annotation.mkString("\n").asJson,
                "type" -> Json.obj(
                    "type" -> typeName.asJson,
                    "kind" -> typeKind.asJson,
                    "size" -> size.asJson
                ),
                "numericIdentifier" -> entry.resolvedIdentifier.asJson,
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
        val commandJson = d.commandEntryMap.asJson
        val paramJson = d.paramEntryMap.asJson
        val eventJson = d.eventEntryMap.asJson
        val channelJson = d.channelEntryMap.asJson
        Json.obj(
            "commands" -> commandJson,
            "parameters" -> paramJson,
            "events" -> eventJson,
            "telemtryChannels" -> channelJson
        )
    }
}