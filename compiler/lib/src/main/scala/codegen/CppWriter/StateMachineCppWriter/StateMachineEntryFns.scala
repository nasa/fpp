package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** A visitor for generating state machine enter functions */
case class StateMachineEntryFns(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
) extends StateMachineCppVisitor(s, aNode) {

  def write = defStateMachineAnnotatedNode(Nil, aNode)

  override def defJunctionAnnotatedNode(
    mm: List[CppDoc.Class.Member],
    aNode: Ast.Annotated[AstNode[Ast.DefJunction]]
  ) = {
    val data = aNode._2.data
    val junctionSym = StateMachineSymbol.Junction(aNode)
    val junctionName = writeSmSymbolName(junctionSym)
    val te = StateMachineTypedElement.Junction(aNode)
    val typeOpt = sma.typeOptionMap(te)
    val valueArgOpt = typeOpt.map(_ => valueParamName)
    val guardSym = sma.getGuardSymbol(data.guard)
    val writeJunctionTransition = writeTransition (signalParamName) (valueArgOpt)
    val ifTransition = sma.flattenedJunctionTransitionMap(data.ifTransition)
    val elseTransition = sma.flattenedJunctionTransitionMap(data.elseTransition)
    val member = functionClassMember(
      Some(s"Enter junction $junctionName"),
      getEnterFunctionName(junctionSym),
      getParamsWithTypeOpt(typeOpt),
      CppDoc.Type("void"),
      wrapInIfElse(
        writeGuardCall (signalParamName) (valueArgOpt) (guardSym),
        writeJunctionTransition (ifTransition),
        writeJunctionTransition (elseTransition)
      )
    )
    member :: mm
  }

  override def defStateAnnotatedNodeInner(
    mm: List[CppDoc.Class.Member],
    aNode: Ast.Annotated[AstNode[Ast.DefState]],
    substates: List[Ast.Annotated[AstNode[Ast.DefState]]]
  ) = {
    val members = super.defStateAnnotatedNodeInner(mm, aNode, substates)
    val initSpecifier = State.getInitialSpecifier(aNode._2.data).get._2.data
    val transition = initSpecifier.transition.data
    val actionSymbols = transition.actions.map(sma.getActionSymbol)
    val targetSymbol = sma.useDefMap(transition.target.id)
    val initial = List.concat(
      actionSymbols.flatMap(writeActionCall (signalParamName) (None)),
      writeEnterCall (signalParamName) (None) (targetSymbol)
    )
    val member = getStateEnterFn(aNode, initial)
    member :: members
  }

  override def defStateAnnotatedNodeLeaf(
    mm: List[CppDoc.Class.Member],
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val members = super.defStateAnnotatedNodeLeaf(mm, aNode)
    val stateSym = StateMachineSymbol.State(aNode)
    val update = writeStateUpdate(stateSym)
    val member = getStateEnterFn(aNode, update)
    member :: members
  }

  private def getStateEnterFn(
    aNode: Ast.Annotated[AstNode[Ast.DefState]],
    initialOrUpdate: List[Line]
  ): CppDoc.Class.Member = {
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
        entryActionSymbols.flatMap(writeActionCall (signalParamName) (None)),
        initialOrUpdate
      )
    )
  }

}
