package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import io.circe.Decoder.state

case class ComponentExternalStateMachines(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getConstantMembers: List[CppDoc.Class.Member] = {
    lazy val lcm = linesClassMember(
      List.concat(
        Line.blank :: lines(s"//! State machine identifiers"),
        wrapInNamedEnum(
          "SmId",
          externalSmInstancesByName.map((name, _) => line(s"STATE_MACHINE_${name.toUpperCase},"))
        )
      )
    )
    guardedList (hasExternalStateMachineInstances) (List(lcm))
  }

  def getFunctionMembers: List[CppDoc.Class.Member] = {
    List.concat(
      getOverflowHooks,
      getSignalSendMember
    )
  }

  /** Gets the state machine interfaces */
  def getSmInterfaces: String =
    externalSmSymbols.map(symbol => s", public ${s.writeSymbol(symbol)}_Interface").
      sorted.mkString

  def getVariableMembers: List[CppDoc.Class.Member] =  {
    val members = externalSmInstancesByName.map(
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

  /** Writes the dispatch case, if any, for state machine instances */
  def writeDispatch: List[Line] = {
    lazy val caseBody = 
      Line.blank ::
      List.concat(
        lines(
          s"""|// Deserialize the state machine ID to an FwEnumStoreType
              |FwEnumStoreType enumStoreSmId = 0;
              |Fw::SerializeStatus deserStatus = msg.deserialize(enumStoreSmId);
              |FW_ASSERT(
              |  deserStatus == Fw::FW_SERIALIZE_OK,
              |  static_cast<FwAssertArgType>(deserStatus)
              |);
              |// Cast it to the correct type
              |SmId stateMachineId = static_cast<SmId>(enumStoreSmId);
              |
              |// Deserialize the state machine signal to an FwEnumStoreType.
              |// This value will be cast to the correct type in the
              |// switch statement that calls the state machine update function.
              |FwEnumStoreType enumStoreSmSignal = 0;
              |deserStatus = msg.deserialize(enumStoreSmSignal);
              |FW_ASSERT(
              |  deserStatus == Fw::FW_SERIALIZE_OK,
              |  static_cast<FwAssertArgType>(deserStatus)
              |);
              |
              |// Deserialize the state machine data
              |Fw::SmSignalBuffer data;
              |deserStatus = msg.deserialize(data);
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
              |);"""
        ),
        writeStateMachineUpdate,
        lines("\nbreak;")
      )
    lazy val caseStmt =
      Line.blank ::
      line(s"// Handle state machine signals") ::
      wrapInScope(
        s"case $externalStateMachineCppConstantName: {",
        caseBody,
        "}"
      )
    guardedList (hasExternalStateMachineInstances) (caseStmt)
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
        smi => getVirtualOverflowHook(
          smi.getName,
          MessageType.StateMachine,
          ComponentExternalStateMachines.signalParams(s, smi.symbol)
        )
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
              |_status = msg.serialize(static_cast<FwEnumStoreType>($externalStateMachineCppConstantName));
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
          ComponentExternalStateMachines.signalParams(s, smi.symbol)
        )
      )

      functionClassMember(
        Some(s"State machine base-class function for sendSignals"),
        s"${smi.getName}_stateMachineInvoke",
        ComponentExternalStateMachines.signalParams(s, smi.symbol),
        CppDoc.Type("void"),
        Line.blank :: intersperseBlankLines(
          List(serializeCode, sendLogicCode)
        )
      )

    }

    addAccessTagAndComment(
      "PROTECTED",
      "State machine function to push signals to the input queue",
      guardedList (hasExternalStateMachineInstances) (members)
    )
  }

  private def writeStateMachineUpdate: List[Line] =
    Line.blank ::
    line("// Call the state machine update function") ::
    wrapInSwitch(
      "stateMachineId",
      List.concat(
        stateMachineInstances.flatMap((smi) => {
          val smName = s.writeSymbol(smi.symbol)
          val enumName = s.getName(smi.symbol)
          Line.blank ::
          lines(
            s"""|case STATE_MACHINE_${smi.getName.toUpperCase}: {
                |  ${smName}_Interface::${enumName}_Signals signal =
                |    static_cast<${smName}_Interface::${enumName}_Signals>(enumStoreSmSignal);
                |  this->m_stateMachine_${smi.getName}.update(stateMachineId, signal, data);
                |  break;
                |}"""
          )
        }),
        Line.blank :: lines(
          s"""|default:
              |  return MSG_DISPATCH_ERROR;"""
        )
      )
    )

}

object ComponentExternalStateMachines {

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
        CppDoc.Type("const Fw::SmSignalBuffer&"),
        "data",
        Some("The state machine data")
      )
    )
  }

}
