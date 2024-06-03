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

      val (_, defComponent, _) = aNode // Extract the components of the tuple

      val instances = defComponent.data.members.collect {
        case Ast.ComponentMember((_, Ast.ComponentMember.SpecStateMachineInstance(node), _)) => node
      }
    
      val smInstances = instances.map(_.data.name)

      val smDefs = instances.map(_.data.statemachine.data.toIdentList).flatten

      println(s"state machine instances = ${smInstances}")
      println(s"smDefs = ${smDefs}")

      List(linesClassMember(List(Line("getVariableMembers"))))
  }

}

