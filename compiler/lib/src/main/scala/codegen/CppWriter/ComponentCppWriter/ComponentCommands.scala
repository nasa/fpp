package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component commands */
case class ComponentCommands (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val sortedCmds = component.commandMap.toList.sortBy(_._1)

  private val nonParamCmds = sortedCmds.map((opcode, cmd) => cmd match {
    case c: Command.NonParam => Some((opcode, c))
    case _ => None
  }).filter(_.isDefined).map(_.get)

  private val cmdAnnotationMap = component.commandMap.map((opcode, cmd) => {
    val strOpt = cmd match {
      case Command.NonParam(aNode, _) => AnnotationCppWriter.asStringOpt(aNode)
      case Command.Param(aNode, _) => AnnotationCppWriter.asStringOpt(aNode)
    }
    (opcode, strOpt)
  })

  private val cmdParamMap = nonParamCmds.map((opcode, cmd) => {(
    opcode,
    writeFormalParamList(
      cmd.aNode._2.data.params,
      s,
      Nil,
      Some("Fw::CmdStringArg"),
      CppWriterLineUtils.Value
    )
  )}).toMap

  private val opcodeParam = CppDoc.Function.Param(
    CppDoc.Type("FwOpcodeType"),
    "opCode",
    Some("The opcode")
  )

  private val cmdSeqParam = CppDoc.Function.Param(
    CppDoc.Type("U32"),
    "cmdSeq",
    Some("The command sequence number")
  )

  def getConstantMembers: List[CppDoc.Class.Member] = {
    if !(hasCommands || hasParameters) then Nil
    else List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            Line.blank :: lines(s"//! Command opcodes"),
            wrapInEnum(
              lines(
                sortedCmds.map((opcode, cmd) =>
                  writeEnumConstant(
                    commandConstantName(cmd),
                    opcode,
                    cmdAnnotationMap(opcode),
                    ComponentCppWriterUtils.Hex
                  )
                ).mkString("\n")
              )
            )
          ).flatten
        )
      )
    )
  }

  def getPublicFunctionMembers: List[CppDoc.Class.Member] = {
    if !(hasCommands || hasParameters) then Nil
    else
      getRegFunction
  }

  def getProtectedFunctionMembers: List[CppDoc.Class.Member] = {
    if !(hasCommands || hasParameters) then Nil
    else List(
      getResponseFunction,
      getFunctions
    ).flatten
  }

  private def getFunctions: List[CppDoc.Class.Member] = {
    if !hasCommands then Nil
    else List(
      getHandlers,
      getHandlerBases,
      getPreMsgHooks
    ).flatten
  }

  private def getRegFunction: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("public"),
            CppDocWriter.writeBannerComment(
              "Command registration"
            ),
          ).flatten
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some(
            s"""|\\brief Register commands with the Command Dispatcher
                |
                |Connect the dispatcher first
                |"""
          ),
          "regCommands",
          Nil,
          CppDoc.Type("void"),
          Nil
        )
      )
    )
  }

  private def getHandlers: List[CppDoc.Class.Member] = {
    List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment(
                "Command handlers to implement"
              ),
            ).flatten
          )
        ),
      ),
      nonParamCmds.map((opcode, cmd) =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(
              s"Handler for command ${cmd.getName}" +
                getComment(opcode)
            ),
            commandHandlerName(cmd.getName),
            List(
              List(
                opcodeParam,
                cmdSeqParam,
              ),
              cmdParamMap(opcode)
            ).flatten,
            CppDoc.Type("void"),
            Nil,
            CppDoc.Function.PureVirtual
          )
        )
      )
    ).flatten
  }

  private def getHandlerBases: List[CppDoc.Class.Member] = {
    List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment(
                """|Command handler base-class functions.
                   |Call these functions directly to bypass the command input port.
                   |"""
              ),
            ).flatten
          )
        ),
      ),
      nonParamCmds.map((opcode, cmd) =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(
              s"Base-class handler function for command ${cmd.getName}" +
                getComment(opcode)
            ),
            commandHandlerBaseName(cmd.getName),
            List(
              opcodeParam,
              cmdSeqParam,
              CppDoc.Function.Param(
                CppDoc.Type("Fw::CmdArgBuffer&"),
                "args",
                Some("The command argument buffer")
              )
            ),
            CppDoc.Type("void"),
            Nil
          )
        )
      )
    ).flatten
  }

  private def getResponseFunction: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("PROTECTED"),
            CppDocWriter.writeBannerComment(
              "Command response"
            ),
          ).flatten
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some(
            "Emit command response"
          ),
          "cmdResponse_out",
          List(
            opcodeParam,
            cmdSeqParam,
            CppDoc.Function.Param(
              CppDoc.Type("Fw::CmdResponse"),
              "response",
              Some("The command response")
            )
          ),
          CppDoc.Type("void"),
          Nil
        )
      )
    )
  }

  private def getPreMsgHooks: List[CppDoc.Class.Member] = {
    List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment(
                s"""|Pre-message hooks for async commands.
                    |Each of these functions is invoked just before processing the
                    |corresponding command. By default they do nothing. You can
                    |override them to provide specific pre-command behavior.
                    |"""
              ),
            ).flatten
          )
        )
      ),
      nonParamCmds.filter((_, cmd) => cmd.kind match {
        case Command.NonParam.Async(_, _) => true
        case _ => false
      }).map((_, cmd) =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Pre-message hook for command ${cmd.getName}"),
            inputPortHookName(cmd.getName),
            List(
              opcodeParam,
              cmdSeqParam
            ),
            CppDoc.Type("void"),
            Nil,
            CppDoc.Function.Virtual
          )
        )
      )
    ).flatten
  }

  private def getComment(opcode: Command.Opcode) =
    cmdAnnotationMap(opcode) match {
      case Some(str) => s"\n\n$str"
      case None => ""
    }

  // Get the name for a command handler
  private def commandHandlerName(name: String) =
    s"${name}_cmdHandler"

  // Get the name for a command handler base-class function
  private def commandHandlerBaseName(name: String) =
    s"${name}_cmdHandlerBase"

  // Get the name for a command opcode constant
  private def commandConstantName(cmd: Command) = {
    val name = cmd match {
      case Command.NonParam(_, _) => cmd.getName
      case Command.Param(aNode, kind) =>
        val kindStr = kind match {
          case Command.Param.Save => "SAVE"
          case Command.Param.Set => "SET"
        }
        s"${aNode._2.data.name}_$kindStr"
    }

    s"OPCODE_${name.toUpperCase}"
  }

}
