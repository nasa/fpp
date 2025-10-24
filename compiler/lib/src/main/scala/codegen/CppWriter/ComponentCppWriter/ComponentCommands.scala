package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component commands */
case class ComponentCommands (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getConstantMembers: List[CppDoc.Class.Member] =
    guardedList (hasCommands || hasParameters) (List(getOpcodes))

  def getPublicFunctionMembers: List[CppDoc.Class.Member] =
    guardedList (hasCommands || hasParameters) (getRegFunction)

  def getProtectedFunctionMembers: List[CppDoc.Class.Member] =
    guardedList (hasCommands || hasParameters) (
      List.concat(
        getResponseFunction,
        getFunctions
      )
    )

  private def getOpcodes = linesClassMember(
    Line.blank ::
    line(s"//! Command opcodes") ::
    wrapInEnum(sortedCmds.flatMap(writeOpcodeConstant))
  )

  private def writeOpcodeConstant(
    opcode: Command.Opcode,
    cmd: Command
  ) = writeEnumConstant(
    commandConstantName(cmd),
    opcode,
    cmd match {
      case Command.NonParam(aNode, _) =>
        AnnotationCppWriter.asStringOpt(aNode)
      case Command.Param(aNode, kind) =>
        val kindString = getCmdParamKindString(kind)
        val name = aNode._2.data.name
        Some(s"Opcode to $kindString parameter $name")
    },
    CppWriterUtils.Hex
  )

  private def getFunctions: List[CppDoc.Class.Member] =
    List.concat(
      getHandlers,
      getHandlerBases,
      getPreMsgHooks,
      getOverflowHooks,
    )

  private def getRegFunction: List[CppDoc.Class.Member] = {
    val portName = cmdRegPort.get.getUnqualifiedName
    val isConnectedName = outputPortIsConnectedName(portName)
    val invoker = outputPortInvokerName(portName)
    val body = intersperseBlankLines(
      List(
        lines(s"FW_ASSERT(this->$isConnectedName(0));"),
        intersperseBlankLines(
          sortedCmds.map((_, cmd) =>
            lines(
              s"""|this->$invoker(
                  |  0,
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

  private def getHandlers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "protected",
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

  private def writeAsyncHandlerBaseBody(
    cmd: Command,
    priority: Option[BigInt],
    queueFull: Ast.QueueFull
  ) = {
    val hookName = inputPortHookName(cmd.getName)
    val cppConstantName = commandCppConstantName(cmd)
    intersperseBlankLines(
      List(
        lines(
          s"""|// Call pre-message hook
              |this->$hookName(opCode,cmdSeq);
              |
              |// Defer deserializing arguments to the message dispatcher
              |// to avoid deserializing and reserializing just for IPC
              |ComponentIpcSerializableBuffer msg;
              |Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;
              |
              |// Serialize for IPC
              |_status = msg.serializeFrom(static_cast<FwEnumStoreType>($cppConstantName));
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
              s"""|_status = msg.serializeFrom($s);
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
  }

  private def writeSyncOrGuardedHandlerBaseBody(
    opcode: Command.Opcode,
    cmd: Command.NonParam
  ) = {
    val cmdParamTypes = cmdParamTypeMap(opcode)
    val cmdHasParams = !cmdParamTypes.isEmpty
    val cmdIsGuarded = cmd.kind == Command.NonParam.Guarded
    val handlerName = commandHandlerName(cmd.getName)
    val isConnectedName = outputPortIsConnectedName(cmdRespPort.get.getUnqualifiedName)
    intersperseBlankLines(
      List(
        guardedList (cmdHasParams) (
          lines(
            """|// Deserialize the arguments
               |Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;
               |
               |// Reset the buffer
               |args.resetDeser();
               |"""
          )
        ),
        intersperseBlankLines(
          cmdParamTypes.map((n, tn, _) =>
            lines(
              s"""|$tn $n;
                  |_status = args.deserializeTo($n);
                  |if (_status != Fw::FW_SERIALIZE_OK) {
                  |  if (this->$isConnectedName(0)) {
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
              |  if (this->$isConnectedName(0)) {
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
        guardedList (cmdIsGuarded) (lines("this->lock();")),
        writeFunctionCall(
          s"this->$handlerName",
          List("opCode, cmdSeq"),
          cmdParamTypes.map(_._1)
        ),
        guardedList (cmdIsGuarded) (lines("this->unLock();"))
      )
    )
  }

  private def getHandlerBaseForNonParamCommand(
    opcode: Command.Opcode,
    cmd: Command.NonParam
  ) = {
    val cmdParamTypes = cmdParamTypeMap(opcode)
    val handlerName = commandHandlerName(cmd.getName)
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
        case Command.NonParam.Async(priority, queueFull) =>
          writeAsyncHandlerBaseBody(cmd, priority, queueFull)
        case _ =>
          writeSyncOrGuardedHandlerBaseBody(opcode, cmd)
      }
    )
  }

  private def getHandlerBases: List[CppDoc.Class.Member] =
    wrapClassMembersInIfDirective(
      "#if !FW_DIRECT_PORT_CALLS // TODO",
      addAccessTagAndComment(
        "protected",
        """|Command handler base-class functions
           |
           |Call these functions directly to bypass the command input port
           |""",
        nonParamCmds.map(getHandlerBaseForNonParamCommand)
      ),
      CppDoc.Lines.Cpp
    )

  private def getResponseFunction: List[CppDoc.Class.Member] =
    wrapClassMembersInIfDirective(
      "#if !FW_DIRECT_PORT_CALLS // TODO",
      addAccessTagAndComment(
        "protected",
        "Command response",
        List(
          functionClassMember(
            Some("Emit command response"),
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
      ),
      CppDoc.Lines.Cpp
    )

  private def getPreMsgHooks: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
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
      "protected",
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
