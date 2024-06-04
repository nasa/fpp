package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component port instances */
case class ComponentStateMachines(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getVariableMembers: List[CppDoc.Class.Member] = {
          genInstantiations ++
          genEnumerations
  }


  def genInstantiations: List[CppDoc.Class.Member] = {

      val (_, defComponent, _) = aNode // Extract the components of the tuple

      val instances = defComponent.data.members.collect {
        case Ast.ComponentMember((_, Ast.ComponentMember.SpecStateMachineInstance(node), _)) => node
      }

      val smInstances = instances.map(_.data.name)
      val smDefs = instances.flatMap(_.data.statemachine.data.toIdentList)

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

      val (_, defComponent, _) = aNode // Extract the components of the tuple

      val instances = defComponent.data.members.collect {
        case Ast.ComponentMember((_, Ast.ComponentMember.SpecStateMachineInstance(node), _)) => node
      }

      val smInstances = instances.map(_.data.name).map(x => x.toUpperCase).map(x => line(x))

      addAccessTagAndComment(
        "PRIVATE",
        s"State machine Enumeration",
        List(linesClassMember(smInstances)),
        CppDoc.Lines.Hpp
      )

  }

}
 
