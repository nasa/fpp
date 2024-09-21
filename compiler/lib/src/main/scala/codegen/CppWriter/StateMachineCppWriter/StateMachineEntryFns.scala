package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** A visitor for generating state machine enter functions */
case class StateMachineEntryFns(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
) extends StateMachineCppVisitor(s, aNode) {

  def write = defStateMachineAnnotatedNode(Nil, aNode).reverse

  override def defJunctionAnnotatedNode(
    mm: List[CppDoc.Class.Member],
    aNode: Ast.Annotated[AstNode[Ast.DefJunction]]
  ) = default(mm) // TODO

  override def defStateAnnotatedNodeInner(
    mm: List[CppDoc.Class.Member],
    aNode: Ast.Annotated[AstNode[Ast.DefState]],
    substates: List[Ast.Annotated[AstNode[Ast.DefState]]]
  ) = default(mm) // TODO

  override def defStateAnnotatedNodeLeaf(
    mm: List[CppDoc.Class.Member],
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val stateSym = StateMachineSymbol.State(aNode)
    val stateName = writeSmSymbolName(stateSym)
    val soj = StateOrJunction.State(stateSym)
    val entryActionSymbols =
      State.getEntryActions(aNode._2.data).map(sma.getActionSymbol)
    functionClassMember(
      Some(s"Enter state $stateName"),
      getEnterFunctionName(soj),
      Nil,
      CppDoc.Type("void"),
      List.concat(
        entryActionSymbols.flatMap(writeNoArgActionCall),
        writeStateUpdate(stateSym)
      )
    ) :: mm
  }

}
