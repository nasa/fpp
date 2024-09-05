package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Construct the transition graph */
object ConstructTransitionGraph extends TransitionExprAnalyzer {

  override def stateTransitionExpr(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]],
    exprNode: AstNode[Ast.TransitionExpr]
  ): Result = {
    val startNode = getNodeFromSymbol(sma.parentSymbol.get)
    val endNode = getNodeFromExpr(sma, exprNode)
    val info = TransitionGraph.TransitionInfo.State(aNode)
    val arc = TransitionGraph.Arc(startNode, info, endNode)
    val transitionGraph = sma.transitionGraph.addArc(arc)
    Right(sma.copy(transitionGraph = transitionGraph))
  }

  override def initialTransitionExpr(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]],
    exprNode: AstNode[Ast.TransitionExpr]
  ): Result = {
    // Construct the end node
    val endNode = getNodeFromSymbol(sma.useDefMap(exprNode.data.destination.id))
    // Update the transition graph
    val transitionGraph = sma.parentSymbol match {
      // We are in a state S. Record the arc from S.
      case Some(startSymbol) =>
        val startNode = getNodeFromSymbol(startSymbol)
        val info = TransitionGraph.TransitionInfo.Initial(aNode)
        val arc = TransitionGraph.Arc(startNode, info, endNode)
        sma.transitionGraph.addArc(arc)
      // We are not in a state, so this is the state machine initial
      // transition. Record it.
      case None =>
        sma.transitionGraph.copy(initialNode = Some(endNode))
    }
    Right(sma.copy(transitionGraph = transitionGraph))
  }

  override def junctionTransitionExprs(
    sma: StateMachineAnalysis,
    junction: StateMachineSymbol.Junction,
    ifExprNode: AstNode[Ast.TransitionExpr],
    elseExprNode: AstNode[Ast.TransitionExpr]
  ): Result = for {
    sma <- junctionExpr(sma, junction, ifExprNode)
    sma <- junctionExpr(sma, junction, elseExprNode)
  } yield sma

  private def junctionExpr(
    sma: StateMachineAnalysis,
    junction: StateMachineSymbol.Junction,
    exprNode: AstNode[Ast.TransitionExpr]
  ): Result = {
    val startNode = getNodeFromSymbol(junction)
    val info = TransitionGraph.TransitionInfo.Junction(exprNode)
    val endNode = getNodeFromExpr(sma, exprNode)
    val arc = TransitionGraph.Arc(startNode, info, endNode)
    val transitionGraph = sma.transitionGraph.addArc(arc)
    Right(sma.copy(transitionGraph = transitionGraph))
  }

  private def getNodeFromSymbol(s: StateMachineSymbol): TransitionGraph.Node = {
    val soj = s match {
      case state: StateMachineSymbol.State =>
        StateOrJunction.State(state)
      case junction: StateMachineSymbol.Junction =>
        StateOrJunction.Junction(junction)
      case _ => throw new InternalError("transition should go to state or junction")
    }
    TransitionGraph.Node(soj)
  }

  private def getNodeFromExpr(
    sma: StateMachineAnalysis,
    exprNode: AstNode[Ast.TransitionExpr]
  ): TransitionGraph.Node =
    getNodeFromSymbol(sma.useDefMap(exprNode.data.destination.id))

}
