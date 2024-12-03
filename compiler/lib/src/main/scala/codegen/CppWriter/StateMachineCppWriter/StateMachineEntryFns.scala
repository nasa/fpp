package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** A visitor for generating state machine enter functions */
case class StateMachineEntryFns(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
) extends StateMachineCppVisitor(s, aNode) {

  def write = defStateMachineAnnotatedNode(Nil, aNode)

  override def defChoiceAnnotatedNode(
    mm: List[CppDoc.Class.Member],
    aNode: Ast.Annotated[AstNode[Ast.DefChoice]]
  ) = {
    val data = aNode._2.data
    val choiceSym = StateMachineSymbol.Choice(aNode)
    val choiceName = writeSmSymbolName(choiceSym)
    val te = StateMachineTypedElement.Choice(aNode)
    val typeOpt = sma.typeOptionMap(te)
    val valueArgOpt = typeOpt.map(_ => valueParamName)
    val guardSym = sma.getGuardSymbol(data.guard)
    val writeChoiceTransition = writeTransition (signalParamName) (valueArgOpt)
    val ifTransition = sma.flattenedChoiceTransitionMap(data.ifTransition)
    val elseTransition = sma.flattenedChoiceTransitionMap(data.elseTransition)
    val member = functionClassMember(
      Some(s"Enter choice $choiceName"),
      getEnterFunctionName(choiceSym),
      getParamsWithTypeOpt(typeOpt),
      CppDoc.Type("void"),
      wrapInIfElse(
        writeGuardCall (signalParamName) (valueArgOpt) (guardSym),
        writeChoiceTransition (ifTransition),
        writeChoiceTransition (elseTransition)
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
    val actionComment = line("// Do the actions for the initial transition")
    val actionLines = actionSymbols.flatMap(writeActionCall (signalParamName) (None))
    val enterComment = line("// Enter the target of the initial transition")
    val enterLines = writeEnterCall (signalParamName) (None) (targetSymbol)
    val initial = List.concat(
      Line.addPrefixLine (actionComment) (actionLines),
      Line.addPrefixLine (enterComment) (enterLines)
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
    val updateComment = line("// Update the state")
    val updateLines = writeStateUpdate(stateSym)
    val member = getStateEnterFn(
      aNode,
      Line.addPrefixLine (updateComment) (updateLines)
    )
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
    val entryActionComment = line("// Do the entry actions")
    val entryActionLines =
        entryActionSymbols.flatMap(writeActionCall (signalParamName) (None))
    functionClassMember(
      Some(s"Enter state $stateName"),
      getEnterFunctionName(stateSym),
      List(signalParam),
      CppDoc.Type("void"),
      List.concat(
        Line.addPrefixLine (entryActionComment) (entryActionLines),
        initialOrUpdate
      )
    )
  }

}
