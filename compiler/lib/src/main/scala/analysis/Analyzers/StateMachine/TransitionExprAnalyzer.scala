package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze transition expressions */
trait TransitionExprAnalyzer
  extends StateMachineAnalysisVisitor
  with StateAnalyzer
{

  // ----------------------------------------------------------------------
  // Interface methods to override
  // Each of these methods is called when a corresponding transition
  // expression is visited
  // ----------------------------------------------------------------------

  /** A transition expression in an external state transition */
  def stateTransitionExpr(
    sma: StateMachineAnalysis,
    stateTransitionNode: AstNode[Ast.SpecStateTransition],
    exprNode: AstNode[Ast.TransitionExpr]
  ): Result = default(sma)

  /** A transition expression in an initial transition */
  def initialTransitionExpr(
    sma: StateMachineAnalysis,
    initialTransitionNode: AstNode[Ast.SpecInitialTransition],
    exprNode: AstNode[Ast.TransitionExpr]
  ): Result = default(sma)

  /** The transition expressions in a junction */
  def junctionTransitionExprs(
    sma: StateMachineAnalysis,
    junctionNode: StateMachineSymbol.Junction,
    ifExprNode: AstNode[Ast.TransitionExpr],
    elseExprNode: AstNode[Ast.TransitionExpr]
  ): Result = default(sma)

  // ----------------------------------------------------------------------
  // Implementation using StateMachineAnalysisVisitor
  // ----------------------------------------------------------------------

  override def defJunctionAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefJunction]]
  ) = {
    val data = aNode._2.data
    junctionTransitionExprs(
      sma,
      StateMachineSymbol.Junction(aNode),
      data.ifTransition,
      data.elseTransition
    )
  }

  override def specInitialTransitionAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]]
  ) = {
    val node = aNode._2
    val data = node.data
    initialTransitionExpr(sma, node, data.transition)
  }

  override def specStateTransitionAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]]
  ) = {
    val node = aNode._2
    val data = node.data
    data.transitionOrDo match {
      case Ast.TransitionOrDo.Transition(transition) =>
        stateTransitionExpr(sma, node, transition)
      case _ => Right(sma)
    }
  }

}
