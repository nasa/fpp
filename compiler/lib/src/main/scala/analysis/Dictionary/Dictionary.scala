package fpp.compiler.analysis.dictionary

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.analysis._

// need array and struct

case class FormalParam(
    identifier: String = "",
    description: String = "",
    paramType: String = "",
    paramKind: String = "",
    ref: Option[Boolean] = None,
    size: Option[BigInt] = None 
)

case class Parameter(
    identifier: String = "",
    description: String = "",
    paramType: String = "",
    default: Option[String] = None,
    numericIdentifier: Option[Int] = None,
    setOpcode: Option[Int] = None,
    saveOpcode: Option[Int] = None
)

case class Command(
    identifier: String = "",
    commandKind: String = "",
    opcode: BigInt = 0,
    description: String = "",
    formalParams: List[FormalParam] = List(),
    priority: Option[BigInt] = None,
    queueFullBehavior: Option[String] = Some("assert")
)

case class LimitValue(
    yellow: Int = 0,
    orange: Int = 0,
    red: Int = 0
)

case class Limit(
    lowLimit: Option[LimitValue] = None,
    highLimit: Option[LimitValue] = None
)

case class TelemtryChannel(
    identifier: String = "",
    description: String = "",
    channelType: String = "",
    numericIdentifier: Int = 0,
    telemetryUpdate: Option[String] = None,
    formatString: Option[String] = None,
    limit: Option[Limit] = None
)

case class Event(
    identifier: String = "",
    description: String = "",
    severity: String = "",
    formalParams: List[FormalParam] = List(),
    numericIdentifier: Int = 0,
    formatString: Option[String] = None,
    throttle: Option[Int] = None
)

case class Record(
    identifier: String = "",
    description: String = "",
    recordType: String = "",
    array: Option[Boolean] = None,
    numericIdentifier: Int = 0
)

case class Container(
    identifier: String = "",
    description: String = "",
    numericIdentifier: Int = 0,
    defaultPriority: Option[BigInt] = None
)

// /** Dictionary data structure */
// extract command, event, channel, parameter information from analysis data structure
case class Dictionary(
    /** The commands in the dictionary */
    commands: List[Command] = List(),
    /** The events in the dictionary */
    events: List[Event] = List(),
    /** The telemetry channels in the dictionary */
    telemChannels: List[TelemtryChannel] = List(),
    /** The parameters in the dictionary */
    parameters: List[Parameter] = List(),
    /** The records in the dictionary */
    records : List[Record] = List(),
    /** The containers in the dictionary */
    container: List[Container] = List()
) {
    //* Returns a list of formal parameters */
    def getFormalParameters(params: Ast.FormalParamList): List[FormalParam] = {
        var resultList = List.empty[FormalParam]
        params.length match {
            case 0 => return List.empty[FormalParam]
            case _ => for (paramEntry <- params) {
                val (_, elem, annotation) = paramEntry
                val description = annotation.mkString("\n")
                val AstNode(Ast.FormalParam(kind, identifier, typeNameNode), _) = elem
                val (typeName, typeKind, size) = typeNameNode match {
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
                resultList :+= FormalParam(identifier, description, typeName, typeKind, None, size)
            }
        }
        return resultList
    }
    //* Adds a command to the dictionary list of commands */
    def addCommand(commandEntry: (fpp.compiler.analysis.Command.Opcode, fpp.compiler.analysis.Command)): Dictionary = {
        val opcode = commandEntry._1 // want to keep as decimal number
        val command = commandEntry._2
        command match {
            case fpp.compiler.analysis.Command.NonParam(aNode, kind) => {
                val (annotation, node, _) = aNode
                val data = node.data
                val identifier = data.name // TODO: need to use qualified name (ie: module.component.commandName)
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
                val formalParams = getFormalParameters(data.params)
                val newCommand = Command(identifier, commandKind, opcode, description, formalParams, priority, queueFullBehavior)
                this.copy(commands = newCommand :: this.commands)
            }
            // case where command is param set/save command
            case fpp.compiler.analysis.Command.Param(aNode, kind) => {
                println("TODO")
                this.copy(commands = Command("", "", 0, "", List(), None, Some("assert")) :: this.commands)
            }
        }
    }

    //* Add a parameter to the dictionary list of parameters */
    def addParameter(paramEntry: (fpp.compiler.analysis.Param.Id, fpp.compiler.analysis.Param)): String = {
        val identifier = paramEntry._1 // want to keep as decimal number
        val param = paramEntry._2

        val paramType = param.paramType.toString

        // need to go from default value (whatever that may be) -> parameter default value
        param.default match {
            case Some(value) => println(value)
            case None => None
        }
        val setOpcode = param.setOpcode
        val saveOpcode = param.saveOpcode
        ""
    }
}