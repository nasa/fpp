package fpp.compiler.analysis.dictionary

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.analysis._

// need array and struct

case class FormalParam(
    identifier: String = "",
    description: String = "",
    paramType: String = "",
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


/** Dictionary data structure */
case class Dictionary(
    commands: List[Command],
    events: List[Event],
    channels: List[TelemtryChannel],
    parameters: List[Parameter],
    // need array and struct
)

// input: analysis

// need to do:
// extract command, event, channel, parameter information from analysis data structure
object GenerateDictionary {
    def createDictionary(componentMap: Map[Symbol.Component, Component]): Unit = {
        for((componentSymbol, component) <- componentMap) {
            // call func to gather commmands
            val commandList = component.commandMap.toList.map(extractCommand())
            // call func to gather events
            // call func to gather channels
            // call func to gather params
            // create Dictionary object with above properties
            // add dicitionary to component to dictionary mapping
        }
        // return component to dictionary mapping
    }

    def getFormalParameters(params: Ast.FormalParamList): List[FormalParam] = {
        var resultList = List.empty[FormalParam]
        params.length match {
            case 0 => return List.empty[FormalParam]
            case _ => for (paramEntry <- params) {
                val (_, elem, annotation) = paramEntry
                val description = annotation.mkString("\n")
                val AstNode(Ast.FormalParam(kind, identifier, typeNameNode), _) = elem
                val (typeName, size) = typeNameNode match {
                    case AstNode(Ast.TypeNameString(value), _) => {
                        val stringSize = value match {
                            case Some(AstNode(Ast.ExprLiteralInt(sizeVal), _)) => Some(BigInt(sizeVal))
                            case None => None
                            case _ => None
                        }
                        ("string", stringSize)
                    }
                    case AstNode(Ast.TypeNameInt(value), _) => {
                        val stringVal = value.toString
                        val intSize = if (stringVal.startsWith("I")) stringVal.split("I").tail else stringVal.split("U").tail
                        (stringVal, Some(BigInt(intSize.mkString(""))))
                    }
                    case AstNode(Ast.TypeNameFloat(value), _) => {
                        val stringVal = value.toString
                        val floatSize = stringVal.split("F").tail
                        (stringVal, Some(BigInt(floatSize.mkString(""))))
                    }
                    case _ => ("not implemented", None)
                }
                resultList :+= FormalParam(identifier, description, typeName, None, size)
            }
        }
        return resultList
    }
    // to try:
    def extractCommand() (commandEntry: (fpp.compiler.analysis.Command.Opcode, fpp.compiler.analysis.Command)): Unit = {
        val opcode = commandEntry._1 // want to keep as decimal number
        val command = commandEntry._2
        command match {
            case fpp.compiler.analysis.Command.NonParam(aNode, kind) => {
                val (annotation, node, _) = aNode
                val data = node.data
                val mnemonic = data.name // TODO: need to use qualified name (ie: module.component.commandName)
                val description = annotation.mkString("\n")
                // kind can either be: async, guarded, or sync
                // can I use toString?
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
                return Command(mnemonic, commandKind, opcode, description, formalParams, priority, queueFullBehavior)
            }
            case fpp.compiler.analysis.Command.Param(aNode, kind) => {
                println("TODO")
            }
        }
    }
}