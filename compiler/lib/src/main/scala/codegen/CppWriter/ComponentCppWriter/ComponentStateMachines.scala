package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

case class ComponentStateMachines(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getConstantMembers: List[CppDoc.Class.Member] = {
    lazy val lcm = linesClassMember(
      List.concat(
        Line.blank :: lines(s"//! State machine identifiers"),
        wrapInNamedEnum(
          "SmId",
          smInstancesByName.map((name, _) => line(s"STATE_MACHINE_${name.toUpperCase},"))
        )
      )
    )
    guardedList (hasStateMachineInstances) (List(lcm))
  }

  def getFunctionMembers: List[CppDoc.Class.Member] = {
    List.concat(
      getOverflowHooks,
      getSignalSendMember
    )
  }

  def getInternalInterfaceHandler: List[Line] =
    wrapInSwitch(
      "sig.getsmId()",
      smInstancesByName.flatMap((name, _) =>
        lines(
          s"""|case STATE_MACHINE_${name.toUpperCase}:
              |  this->m_stateMachine_$name.update(&sig);
              |  break;
          """
        )
      )
    )

  /** Gets the state machine interfaces */
  def getSmInterfaces: String =
    smSymbols.map(symbol => s", public ${s.writeSymbol(symbol)}_Interface").
      sorted.mkString

  def getVariableMembers: List[CppDoc.Class.Member] =  {
    val members = smInstancesByName.map(
      (name, smi) => {
        val typeName = s.writeSymbol(smi.symbol)
        linesClassMember(
          Line.blank ::
          lines(
            s"""|//! State machine $name
                |$typeName m_stateMachine_$name;
                |"""
          )
        )
      }
    )
    addAccessTagAndComment(
      "PRIVATE",
      s"State machine instances",
      members,
      CppDoc.Lines.Hpp
    )
  }

  def writeDispatch: List[Line] = {
    lazy val caseBody = List.concat(
      lines(
        s"""|Fw::SMSignals sig;
            |deserStatus = msg.deserialize(sig);
            |
            |FW_ASSERT(
            |  Fw::FW_SERIALIZE_OK == deserStatus,
            |  static_cast<FwAssertArgType>(deserStatus)
            |);
            |
            |// Make sure there was no data left over.
            |// That means the buffer size was incorrect.
            |FW_ASSERT(
            |  msg.getBuffLeft() == 0,
            |  static_cast<FwAssertArgType>(msg.getBuffLeft())
            |);
            |
            |// Update the state machine with the signal
            |"""
      ),
      getInternalInterfaceHandler,
      lines("\nbreak;")
    )
    lazy val caseStmt =
      Line.blank ::
      line(s"// Handle state machine signals ") ::
      wrapInScope(
        s"case $stateMachineCppConstantName: {",
        caseBody,
        "}"
      )
    guardedList (hasStateMachineInstances) (caseStmt)
  }

  private def getOverflowHooks: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      """|Overflow hooks for state machine instances marked 'hook'
         |
         |When sending a signal to a state machine instance, if
         |the queue overflows and the instance is marked with 'hook' behavior,
         |the corresponding function here is called.
         |""",
      stateMachineInstances.filter(smi => smi.queueFull == Ast.QueueFull.Hook).map(
        smi => getOverflowHook(
          smi.getName,
          MessageType.StateMachine,
          List(
            CppDoc.Function.Param(
              CppDoc.Type("const Fw::SMSignals&"),
              "sig",
              Some("The signal data")
            )
          )
        )
      ),
      CppDoc.Lines.Hpp
    )

  private def getSignalSendMember: List[CppDoc.Class.Member] = {
    lazy val serializeCode =
          lines(
            s"""|ComponentIpcSerializableBuffer msg;
                |Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;
                |
                |// Serialize the message ID
                |_status = msg.serialize(static_cast<FwEnumStoreType>($stateMachineCppConstantName));
                |FW_ASSERT (
                |  _status == Fw::FW_SERIALIZE_OK,
                |  static_cast<FwAssertArgType>(_status)
                |);
                |
                |// Fake port number to make message dequeue work
                |_status = msg.serialize(static_cast<FwIndexType>(0));
                |FW_ASSERT (
                |  _status == Fw::FW_SERIALIZE_OK,
                |  static_cast<FwAssertArgType>(_status)
                |);
                |
                |_status = msg.serialize(sig);
                |FW_ASSERT(
                |  _status == Fw::FW_SERIALIZE_OK,
                |  static_cast<FwAssertArgType>(_status)
                |);"""
          )


    lazy val switchCode = List.concat(
      lines("const U32 smId = sig.getsmId();"),
      wrapInSwitch(
        "smId",
        List.concat(
          stateMachineInstances.flatMap(
            smi => {
              Line.blank ::
              wrapInScope(
                s"case STATE_MACHINE_${smi.getName.toUpperCase}: {",
                List.concat(
                  writeSendMessageLogic(
                    "msg", smi.queueFull, smi.priority,
                    MessageType.StateMachine, smi.getName, List("sig")
                  ),
                  lines("break;")
                ),
                "}"
              )
            }
          ),
          lines(
            """|
               |default:
               |  FW_ASSERT(0, static_cast<FwAssertArgType>(smId));
               |  break;
               |"""
          )

        )
      )
    )

    lazy val member = functionClassMember(
      Some(s"State machine base-class function for sendSignals"),
      "stateMachineInvoke",
      List(
        CppDoc.Function.Param(
            CppDoc.Type("const Fw::SMSignals&"),
            "sig",
            Some("The state machine signal")
        )
      ),
      CppDoc.Type("void"),
      Line.blank :: intersperseBlankLines(
        List(serializeCode, switchCode)
      )
    )

    addAccessTagAndComment(
      "PROTECTED",
      "State machine function to push signals to the input queue",
      guardedList (hasStateMachineInstances) (List(member))
    )
  }

}
