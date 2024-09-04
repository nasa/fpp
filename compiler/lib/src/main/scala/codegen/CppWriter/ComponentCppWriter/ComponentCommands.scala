package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component commands */
case class ComponentCommands (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getConstantMembers: List[CppDoc.Class.Member] = {
    if !(hasCommands || hasParameters) then Nil
    else List(
      linesClassMember(
        List.concat(
          Line.blank :: lines(s"//! Command opcodes"),
          wrapInEnum(
            sortedCmds.flatMap((opcode, cmd) =>
              writeEnumConstant(
                commandConstantName(cmd),
                opcode,
                cmd match {
                  case Command.NonParam(aNode, _) =>
                    AnnotationCppWriter.asStringOpt(aNode)
                  case Command.Param(aNode, kind) =>
                    Some(s"Opcode to ${getCmdParamKindString(kind)} parameter ${aNode._2.data.name}")
                },
                CppWriterUtils.Hex
              )
            )
          )
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
    List(
      getHandlers,
      getHandlerBases,
      getPreMsgHooks,
      getOverflowHooks,
    ).flatten
  }

  private def getRegFunction: List[CppDoc.Class.Member] = {
    val body = intersperseBlankLines(
      List(
        lines(s"FW_ASSERT(this->${portVariableName(cmdRegPort.get)}[0].isConnected());"),
        intersperseBlankLines(
          sortedCmds.map((_, cmd) =>
            lines(
              s"""|this->${portVariableName(cmdRegPort.get)}[0].invoke(
                  |  this->getIdBase() + ${commandConstantName(cmd)}
                  |);
                  |"""
            )
          )
        )
      )
    )

    addAccessTagAndComment(
      "public",
      "Command registration",
      List(
        functionClassMember(
          Some(
            s"""|\\brief Register commands with the Command Dispatcher
                |
                |Connect the dispatcher first
                |"""
          ),
          "regCommands",
          Nil,
          CppDoc.Type("void"),
          body
        )
      )
    )
  }

  private def getHandlers: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
      "Command handlers to implement",
      nonParamCmds.map((opcode, cmd) =>
        functionClassMember(
          Some(
            addSeparatedString(
              s"Handler for command ${cmd.getName}",
              AnnotationCppWriter.asStringOpt(cmd.aNode)
            )
          ),
          commandHandlerName(cmd.getName),
          opcodeParam :: cmdSeqParam :: cmdParamMap(opcode),
          CppDoc.Type("void"),
          Nil,
          CppDoc.Function.PureVirtual
        )
      ),
      CppDoc.Lines.Hpp
    )
  }

  private def getHandlerBases: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
      """|Command handler base-class functions
         |
         |Call these functions directly to bypass the command input port
         |""",
      nonParamCmds.map((opcode, cmd) =>
        functionClassMember(
          Some(
            addSeparatedString(
              s"Base-class handler function for command ${cmd.getName}",
              AnnotationCppWriter.asStringOpt(cmd.aNode)
            )
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
          cmd.kind match {
            case Command.NonParam.Async(priority, queueFull) => intersperseBlankLines(
              List(
                lines(
                  s"""|// Call pre-message hook
                      |this->${inputPortHookName(cmd.getName)}(opCode,cmdSeq);
                      |
                      |// Defer deserializing arguments to the message dispatcher
                      |// to avoid deserializing and reserializing just for IPC
                      |ComponentIpcSerializableBuffer msg;
                      |Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;
                      |
                      |// Serialize for IPC
                      |_status = msg.serialize(static_cast<FwEnumStoreType>(${commandCppConstantName(cmd)}));
                      |FW_ASSERT (
                      |  _status == Fw::FW_SERIALIZE_OK,
                      |  static_cast<FwAssertArgType>(_status)
                      |);
                      |
                      |// Fake port number to make message dequeue work
                      |FwIndexType port = 0;
                      |"""
                ),
                intersperseBlankLines(
                  List("port", "opCode", "cmdSeq", "args").map(s =>
                    lines(
                      s"""|_status = msg.serialize($s);
                          |FW_ASSERT (
                          |  _status == Fw::FW_SERIALIZE_OK,
                          |  static_cast<FwAssertArgType>(_status)
                          |);
                          |"""
                    )
                  )
                ),
                writeSendMessageLogic(
                  "msg",
                  queueFull,
                  priority,
                  MessageType.Command,
                  cmd.getName,
                  opcodeParam :: cmdSeqParam :: Nil
                )
              )
            )
            case _ => intersperseBlankLines(
              List(
                cmdParamTypeMap(opcode) match {
                  case Nil => Nil
                  case _ => lines(
                    """|// Deserialize the arguments
                       |Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;
                       |
                       |// Reset the buffer
                       |args.resetDeser();
                       |"""
                  )
                },
                intersperseBlankLines(
                  cmdParamTypeMap(opcode).map((n, tn, _) =>
                    lines(
                      s"""|$tn $n;
                          |_status = args.deserialize($n);
                          |if (_status != Fw::FW_SERIALIZE_OK) {
                          |  if (this->${portVariableName(cmdRespPort.get)}[0].isConnected()) {
                          |    this->${portVariableName(cmdRespPort.get)}[0].invoke(
                          |      opCode,
                          |      cmdSeq,
                          |      Fw::CmdResponse::FORMAT_ERROR
                          |    );
                          |  }
                          |  return;
                          |}
                          |"""
                    )
                  )
                ),
                lines(
                  s"""|#if FW_CMD_CHECK_RESIDUAL
                      |// Make sure there was no data left over.
                      |// That means the argument buffer size was incorrect.
                      |if (args.getBuffLeft() != 0) {
                      |  if (this->${portVariableName(cmdRespPort.get)}[0].isConnected()) {
                      |    this->${portVariableName(cmdRespPort.get)}[0].invoke(
                      |      opCode,
                      |      cmdSeq,
                      |      Fw::CmdResponse::FORMAT_ERROR
                      |    );
                      |  }
                      |  return;
                      |}
                      |#endif
                      |"""
                ),
                cmd.kind match {
                  case Command.NonParam.Guarded => lines("this->lock();")
                  case _ => Nil
                },
                writeFunctionCall(
                  s"this->${commandHandlerName(cmd.getName)}",
                  List("opCode, cmdSeq"),
                  cmdParamTypeMap(opcode).map(_._1)
                ),
                cmd.kind match {
                  case Command.NonParam.Guarded => lines("this->unLock();")
                  case _ => Nil
                }
              )
            )
          }
        )
      )
    )
  }

  private def getResponseFunction: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
      "Command response",
      List(
        functionClassMember(
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
          lines(
            s"""|FW_ASSERT(this->${portVariableName(cmdRespPort.get)}[0].isConnected());
                |this->${portVariableName(cmdRespPort.get)}[0].invoke(opCode, cmdSeq, response);
                |"""
          )
        )
      )
    )
  }

  private def getPreMsgHooks: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
      """|Pre-message hooks for async commands
         |
         |Each of these functions is invoked just before processing the
         |corresponding command. By default they do nothing. You can
         |override them to provide specific pre-command behavior.
         |""",
      asyncCmds.map((_, cmd) =>
        functionClassMember(
          Some(s"Pre-message hook for command ${cmd.getName}"),
          inputPortHookName(cmd.getName),
          List(
            opcodeParam,
            cmdSeqParam
          ),
          CppDoc.Type("void"),
          lines(
            s"""|// Defaults to no-op; can be overridden
                |(void) opCode;
                |(void) cmdSeq;
                |"""
          ),
          CppDoc.Function.Virtual
        )
      )
    )
  }

  private def getOverflowHooks: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      """|Overflow hooks for async commands
         |
         |Each of these functions is invoked after an overflow event
         |on a queue when the command is marked with 'hook' overflow
         |behavior.
         |""",
      hookCmds.map(
        (opcode, cmd) => getVirtualOverflowHook(
          cmd.getName,
          MessageType.Command,
          opcodeParam :: cmdSeqParam :: Nil,
        )
      ),
      CppDoc.Lines.Hpp
    )

}
