package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import io.circe.Decoder.state

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
      "stateMachineId",
      stateMachineInstances.flatMap((smi) => {
        val smName = s.writeSymbol(smi.symbol)
        val enumName = s.getName(smi.symbol)

        lines(
          s"""|case STATE_MACHINE_${smi.getName.toUpperCase}: {
              |   // Deserialize the state machine signal
              |   FwEnumStoreType desMsg = 0;
              |   Fw::SerializeStatus deserStatus = msg.deserialize(desMsg);
              |   FW_ASSERT(
              |     deserStatus == Fw::FW_SERIALIZE_OK,
              |     static_cast<FwAssertArgType>(deserStatus)
              |   );
              |   ${smName}_Interface::${enumName}_Signals signal = static_cast<${smName}_Interface::${enumName}_Signals>(desMsg);
              |
              |   // Deserialize the state machine data
              |   Fw::SMSignalBuffer data;
              |   deserStatus = msg.deserialize(data);
              |   FW_ASSERT(
              |     Fw::FW_SERIALIZE_OK == deserStatus,
              |     static_cast<FwAssertArgType>(deserStatus)
              |   );
              |
              |   // Make sure there was no data left over.
              |   // That means the buffer size was incorrect.
              |   FW_ASSERT(
              |     msg.getBuffLeft() == 0,
              |     static_cast<FwAssertArgType>(msg.getBuffLeft())
              |   );
              |
              |   this->m_stateMachine_${smi.getName}.update(stateMachineId, signal, data);
              |   break;
              |}
          """
        )
      }
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
        s"""|
            | FwEnumStoreType desMsg = 0;
            | Fw::SerializeStatus deserStatus = msg.deserialize(desMsg);
            | FW_ASSERT(
            |   deserStatus == Fw::FW_SERIALIZE_OK,
            |   static_cast<FwAssertArgType>(deserStatus)
            | );
            | SmId stateMachineId = static_cast<SmId>(desMsg);
            |
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
      """|Overflow hooks for state machine instances
         |
         |When sending a signal to a state machine instance, if
         |the queue overflows and the instance is marked with 'hook' behavior,
         |the corresponding function here is called.
         |""",
      stateMachineInstances.filter(_.queueFull == Ast.QueueFull.Hook).map(
        smi => {
          getVirtualOverflowHook(
            smi.getName,
            MessageType.StateMachine,
            ComponentStateMachines.signalParams(s, smi.symbol)
          )
        }
      ),
      CppDoc.Lines.Hpp
    )

  private def getSignalSendMember: List[CppDoc.Class.Member] = {
    lazy val members = stateMachineInstances.map { smi =>

      val serializeCode = 
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
              |_status = msg.serialize(static_cast<FwEnumStoreType>(STATE_MACHINE_${smi.getName.toUpperCase}));
              |FW_ASSERT(
              |  _status == Fw::FW_SERIALIZE_OK,
              |  static_cast<FwAssertArgType>(_status)
              |);
              |
              |_status = msg.serialize(static_cast<FwEnumStoreType>(signal));
              |FW_ASSERT(
              |  _status == Fw::FW_SERIALIZE_OK,
              |  static_cast<FwAssertArgType>(_status)
              |);
              |
              |_status = msg.serialize(data);
              |FW_ASSERT(
              |  _status == Fw::FW_SERIALIZE_OK,
              |  static_cast<FwAssertArgType>(_status)
              |);"""
        )

      val sendLogicCode = List.concat(
        writeSendMessageLogic(
          "msg", smi.queueFull, smi.priority,
          MessageType.StateMachine, smi.getName,
          ComponentStateMachines.signalParams(s, smi.symbol)
        )
      )

      functionClassMember(
        Some(s"State machine base-class function for sendSignals"),
        s"${smi.getName}_stateMachineInvoke",
        ComponentStateMachines.signalParams(s, smi.symbol),
        CppDoc.Type("void"),
        Line.blank :: intersperseBlankLines(
          List(serializeCode, sendLogicCode)
        )
      )

    }
   
    addAccessTagAndComment(
      "PROTECTED",
      "State machine function to push signals to the input queue",
      guardedList (hasStateMachineInstances) (members)
    )
  }

}

object ComponentStateMachines {

   def signalParams(s: CppWriterState, sym: Symbol.StateMachine) = {
    val smName = s.writeSymbol(sym)
    val enumName = s.getName(sym)
    List(
      CppDoc.Function.Param(
        CppDoc.Type(s"const ${smName}_Interface::${enumName}_Signals"),
        "signal",
        Some("The state machine signal")
      ),
      CppDoc.Function.Param(
        CppDoc.Type("const Fw::SMSignalBuffer&"),
        "data",
        Some("The state machine data")
      )
    )
  }

}
