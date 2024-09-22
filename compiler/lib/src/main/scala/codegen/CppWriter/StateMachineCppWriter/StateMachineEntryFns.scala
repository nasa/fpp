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
  ) = {
    val junctionSym = StateMachineSymbol.Junction(aNode)
    val junctionName = writeSmSymbolName(junctionSym)
    val te = StateMachineTypedElement.Junction(aNode)
    val typeOpt = sma.typeOptionMap(te)
    functionClassMember(
      Some(s"Enter junction $junctionName"),
      getEnterFunctionName(junctionSym),
      writeParamsWithTypeOpt(typeOpt),
      CppDoc.Type("void"),
      Nil // TODO
    ) :: mm
  }

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
    val entryActionSymbols =
      State.getEntryActions(aNode._2.data).map(sma.getActionSymbol)
    functionClassMember(
      Some(s"Enter state $stateName"),
      getEnterFunctionName(stateSym),
      List(signalParam),
      CppDoc.Type("void"),
      List.concat(
        entryActionSymbols.flatMap(writeNoValueActionCall(signalParamName)),
        writeStateUpdate(stateSym)
      )
    ) :: mm
  }

}
