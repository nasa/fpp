package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

case class ComponentStateMachines(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getVariableMembers: List[CppDoc.Class.Member] = {
          genInstantiations
  }


  def getFunctionMembers: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
      "State machine function to push events to the input queue",
      List(
        functionClassMember(
          Some(
            s"State machine base-class function for sendEvents"
          ),
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
      )
    )
  }

  def writeDispatch: List[Line] = {
      val body = lines(
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
      ) ++ getInternalInterfaceHandler ++ List(line("break;"))

      line(s"// Handle state machine events ") ::
        wrapInScope(
          s"case $stateMachineCppConstantName: {",
          body,
          "}"
        )
  }



  def getInternalInterfaceHandler: List[Line] = {

    wrapInSwitch(
      "ev.getsmId()",
      getInstanceNames.flatMap(x =>
        lines(
          s"""| case ${x.toUpperCase}:
              |   this->$x.update(&ev);
              |   break;
          """
        )
      )
    )

  }


  def genInstantiations: List[CppDoc.Class.Member] = {

      val smLines: List[Line] = getInstanceNames.zip(getSmDefs).map 
          { case (instance, definition) =>
           Line(s"$definition   $instance;")
          }

      addAccessTagAndComment(
        "PRIVATE",
        s"State machine instantiations",
        List(linesClassMember(smLines)),
        CppDoc.Lines.Hpp
      )

  }

  def genEnumerations: List[CppDoc.Class.Member] = {

      val smLines =  
        wrapInNamedEnum(
          "SmId", 
          getInstanceNames.map(x => line(x.toUpperCase + ","))
        )

      addAccessTagAndComment(
        "PROTECTED",
        s"State machine Enumeration",
        List(linesClassMember(smLines)),
        CppDoc.Lines.Hpp
      )
  }

  def getSmNode: List[AstNode[Ast.SpecStateMachineInstance]] = {

      val (_, defComponent, _) = aNode // Extract the components of the tuple

      defComponent.data.members.collect {
        case Ast.ComponentMember((_, Ast.ComponentMember.SpecStateMachineInstance(node), _)) => node
      }

  }

  def getSmDefs: List[String] = 
    getSmNode.map(_.data.statemachine.data.toIdentList).flatten


  def getInstanceNames: List[String] =
       getSmNode.map(_.data.name)


  def getSmInterface: String =
    getSmDefs.toSet.toList.map(x => s", public ${x}If").mkString

}

 
