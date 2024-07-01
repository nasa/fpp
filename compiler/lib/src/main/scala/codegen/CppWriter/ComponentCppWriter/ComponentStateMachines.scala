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
    lazy val member = functionClassMember(
      Some(s"State machine base-class function for sendSignals"),
      "stateMachineInvoke",
      List(
        CppDoc.Function.Param(
            CppDoc.Type("const Fw::SMSignals&"),
            "ev",
            Some("The state machine signal")
        )
      ),
      CppDoc.Type("void"),
      intersperseBlankLines(
        List(
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
                |_status = msg.serialize(ev);
                |FW_ASSERT(
                |  _status == Fw::FW_SERIALIZE_OK,
                |  static_cast<FwAssertArgType>(_status)
                |);
                |
                |"""
          ),
          writeSendMessageLogic("msg", Ast.QueueFull.Assert, Option(1))
        )
      )
    )
    addAccessTagAndComment(
      "PROTECTED",
      "State machine function to push signals to the input queue",
      guardedList (hasStateMachineInstances) (List(member))
    )
  }

  def getInternalInterfaceHandler: List[Line] =
    wrapInSwitch(
      "ev.getsmId()",
      smInstancesByName.flatMap((name, _) =>
        lines(
          s"""|case STATE_MACHINE_${name.toUpperCase}:
              |  this->m_stateMachine_$name.update(&ev);
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
        s"""|Fw::SMSignals ev;
            |deserStatus = msg.deserialize(ev);
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

}
