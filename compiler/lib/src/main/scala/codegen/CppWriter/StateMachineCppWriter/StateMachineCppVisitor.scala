package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** A generic visitor for generating C++ state machine code */
abstract class StateMachineCppVisitor(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
) extends StateMachineCppWriterUtils(s, aNode), AstVisitor {

  type In = List[Line]

  type Out = List[Line]

  def defStateAnnotatedNodeInner(
    lines: List[Line],
    aNode: Ast.Annotated[AstNode[Ast.DefState]],
    substates: List[Ast.Annotated[AstNode[Ast.DefState]]]
  ) = aNode._2.data.members.foldLeft (lines) (matchStateMember)

  def defStateAnnotatedNodeLeaf(
    lines: List[Line],
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = default(lines)

  override def default(lines: List[Line]) = lines

  override def defStateMachineAnnotatedNodeInternal(
    lines: List[Line],
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
    members: List[Ast.StateMachineMember]
  ) = members.foldLeft (lines) (matchStateMachineMember)

  override def defStateAnnotatedNode(
    lines: List[Line],
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = StateMachine.getSubstates(aNode._2.data) match {
    case Nil => defStateAnnotatedNodeLeaf(lines, aNode)
    case substates => defStateAnnotatedNodeInner(lines, aNode, substates)
  }

}
