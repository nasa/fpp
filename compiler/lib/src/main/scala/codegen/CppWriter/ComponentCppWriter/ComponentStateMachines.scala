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


  def getInternalInterfaceHandler: List[CppDoc.Class.Member] = {

    val handlerSpec = Line.blank :: 
      lines(
        s"""|void ${className} :: 
            |  sendEvents_internalInterfaceHandler(const SMEvents& ev)
            |{
            |  U16 id = ev.getsmId();
            |  
            |"""
      )


    val swLines =  wrapInSwitch(
      "id",
      getInstanceNames.flatMap(x =>
        lines(
          s"""| case ${x.toUpperCase}:
              |   this->$x.update(&ev);
              |   break;
          """
        )
      )
    )

    List(
      linesClassMember(
        handlerSpec ++ swLines.map(indentIn) ++ lines("}"),
        CppDoc.Lines.Cpp
      ),
    )
  }


  def genInstantiations: List[CppDoc.Class.Member] = {

      val smDefs = getSmNode.map(_.data.statemachine.data.toIdentList).flatten

      val smLines: List[Line] = getInstanceNames.zip(smDefs).map 
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
        "PRIVATE",
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


  def getInstanceNames: List[String] =
       getSmNode.map(_.data.name)

}
 
