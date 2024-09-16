package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Construct the transition graph */
object ConstructTransitionGraph extends TransitionExprAnalyzer {

  override def defStateAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val sym = StateMachineSymbol.State(aNode)
    val soj = StateOrJunction.State(sym)
    val node = TransitionGraph.Node(soj)
    val transitionGraph = sma.transitionGraph.addNode(node)
    super.defStateAnnotatedNode(
      sma.copy(transitionGraph = transitionGraph),
      aNode
    )
  }

  override def defJunctionAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefJunction]]
  ) = {
    val sym = StateMachineSymbol.Junction(aNode)
    val soj = StateOrJunction.Junction(sym)
    val node = TransitionGraph.Node(soj)
    val transitionGraph = sma.transitionGraph.addNode(node)
    super.defJunctionAnnotatedNode(
      sma.copy(transitionGraph = transitionGraph),
      aNode
    )
  }

  override def stateTransitionExpr(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]],
    exprNode: AstNode[Ast.TransitionExpr]
  ): Result = {
    val endNode = getNodeFromExpr(sma, exprNode)
    val arc = TransitionGraph.Arc.State(sma.parentState.get, aNode, endNode)
    val transitionGraph = sma.transitionGraph.addArc(arc)
    Right(sma.copy(transitionGraph = transitionGraph))
  }

  override def initialTransitionExpr(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]],
    exprNode: AstNode[Ast.TransitionExpr]
  ): Result = {
    // Construct the end node
    val endNode = getNodeFromExpr(sma, exprNode)
    // Update the transition graph
    val transitionGraph = sma.parentState match {
      // We are in a state S. Record the arc from S.
      case Some(startState) =>
        val arc = TransitionGraph.Arc.Initial(startState, aNode, endNode)
        sma.transitionGraph.addArc(arc)
      // We are not in a state, so this is the state machine initial
      // transition. Record it.
      case None =>
        sma.transitionGraph.copy(initialNode = Some(endNode))
    }
    Right(sma.copy(transitionGraph = transitionGraph))
  }

  override def junctionTransitionExpr(
    sma: StateMachineAnalysis,
    junction: StateMachineSymbol.Junction,
    exprNode: AstNode[Ast.TransitionExpr]
  ): Result = {
    val endNode = getNodeFromExpr(sma, exprNode)
    val arc = TransitionGraph.Arc.Junction(junction, exprNode, endNode)
    val transitionGraph = sma.transitionGraph.addArc(arc)
    Right(sma.copy(transitionGraph = transitionGraph))
  }

  private def getNodeFromExpr(
    sma: StateMachineAnalysis,
    exprNode: AstNode[Ast.TransitionExpr]
  ): TransitionGraph.Node = {
    val sym = sma.useDefMap(exprNode.data.target.id)
    val soj = sym match {
      case state: StateMachineSymbol.State =>
        StateOrJunction.State(state)
      case junction: StateMachineSymbol.Junction =>
        StateOrJunction.Junction(junction)
      case _ => throw new InternalError("transition should go to state or junction")
    }
    TransitionGraph.Node(soj)
  }

}
