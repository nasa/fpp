package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** A generic visitor for generating C++ state machine code */
abstract class StateMachineCppVisitor(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
) extends StateMachineCppWriterUtils(s, aNode), AstVisitor {

  type In = List[CppDoc.Class.Member]

  type Out = List[CppDoc.Class.Member]

  def defStateAnnotatedNodeInner(
    mm: List[CppDoc.Class.Member],
    aNode: Ast.Annotated[AstNode[Ast.DefState]],
    substates: List[Ast.Annotated[AstNode[Ast.DefState]]]
  ) = aNode._2.data.members.reverse.foldLeft (mm) (matchStateMember)

  def defStateAnnotatedNodeLeaf(
    mm: List[CppDoc.Class.Member],
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = aNode._2.data.members.reverse.foldLeft (mm) (matchStateMember)

  override def default(mm: List[CppDoc.Class.Member]) = mm

  override def defStateMachineAnnotatedNodeInternal(
    mm: List[CppDoc.Class.Member],
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
    members: List[Ast.StateMachineMember]
  ) = members.foldLeft (mm) (matchStateMachineMember)

  override def defStateAnnotatedNode(
    mm: List[CppDoc.Class.Member],
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = State.getSubstates(aNode._2.data) match {
    case Nil => defStateAnnotatedNodeLeaf(mm, aNode)
    case substates => defStateAnnotatedNodeInner(mm, aNode, substates)
  }

}
