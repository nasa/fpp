package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

case class ComponentStateMachines(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getVariableMembers: List[CppDoc.Class.Member] = {
          genInstantiations ++
          genEnumerations
  }


  def genInstantiations: List[CppDoc.Class.Member] = {

      val smInstances = getInstances.map(_.data.name)
      val smDefs = getInstances.flatMap(_.data.statemachine.data.toIdentList)

      val smLines: List[Line] = smInstances.zip(smDefs).map 
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

      val smInstances = getInstances.map(_.data.name.toUpperCase).map(x => line(s"$x,"))

      val smLines = wrapInNamespace("StateMachine", wrapInNamedEnum("SmId", smInstances))

      addAccessTagAndComment(
        "PRIVATE",
        s"State machine Enumeration",
        List(linesClassMember(smLines)),
        CppDoc.Lines.Hpp
      )
  }

  def getInstances: List[AstNode[Ast.SpecStateMachineInstance]] = {

      val (_, defComponent, _) = aNode // Extract the components of the tuple

      defComponent.data.members.collect {
        case Ast.ComponentMember((_, Ast.ComponentMember.SpecStateMachineInstance(node), _)) => node
      }

  }

}
 
