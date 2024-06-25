package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

case class ComponentStateMachines(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getVariableMembers: List[CppDoc.Class.Member] =  {
    val members = smInstancesByName.map(
      (name, smi) => {
        val typeName = s.writeSymbol(smi.symbol)
        s"$typeName $name;"
      }
    ).map(s => linesClassMember(lines(s)))
    addAccessTagAndComment(
      "PRIVATE",
      s"State machine instantiations",
      members,
      CppDoc.Lines.Hpp
    )
  }

  def getFunctionMembers: List[CppDoc.Class.Member] = {
    lazy val member = functionClassMember(
      Some(s"State machine base-class function for sendEvents"),
      "stateMachineInvoke",
      List(
        CppDoc.Function.Param(
            CppDoc.Type("const Fw::SMEvents&"),
            "ev",
            Some("The state machine event")
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
      "State machine function to push events to the input queue",
      guardedList (hasStateMachineInstances) (List(member))
    )
  }

  def writeDispatch: List[Line] = {
    lazy val caseBody = List.concat(
      lines(
        s"""|Fw::SMEvents ev;
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
            |// Update the state machine with the event
            |
            |"""
      ),
      getInternalInterfaceHandler,
      lines("break;")
    )
    lazy val caseStmt =
      line(s"// Handle state machine events ") ::
      wrapInScope(
        s"case $stateMachineCppConstantName: {",
        caseBody,
        "}"
      )
    guardedList (hasStateMachineInstances) (caseStmt)
  }

  def getInternalInterfaceHandler: List[Line] =
    wrapInSwitch(
      "ev.getsmId()",
      smInstancesByName.flatMap((name, _) =>
        lines(
          s"""|case ${name.toUpperCase}:
              |  this->$name.update(&ev);
              |  break;
          """
        )
      )
    )

  def genEnumerations: List[CppDoc.Class.Member] = {

    lazy val smLines =
      wrapInNamedEnum(
        "SmId",
        smInstancesByName.map((name, _) => line(s"${name.toUpperCase},"))
      )

    addAccessTagAndComment(
      "PROTECTED",
      s"State machine Enumeration",
      guardedList (!smLines.isEmpty) (List(linesClassMember(smLines))),
      CppDoc.Lines.Hpp
    )

  }

  def getSmInterface: String =
    smSymbols.map(symbol => s", public ${s.writeSymbol(symbol)}If").
      sorted.mkString

}
