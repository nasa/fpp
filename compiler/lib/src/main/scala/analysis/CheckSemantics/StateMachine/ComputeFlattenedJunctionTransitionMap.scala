package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute the flattened junction transition map */
object ComputeFlattenedJunctionTransitionMap
  extends TransitionExprAnalyzer
{

  override def junctionTransitionExpr(
    sma: StateMachineAnalysis,
    junction: StateMachineSymbol.Junction,
    exprNode: AstNode[Ast.TransitionExpr]
  ): Result = {
    val transition = {
      val actions = exprNode.data.actions.map(sma.getActionSymbol)
      val target = sma.getStateOrJunction(exprNode.data.target)
      val transition0 = Transition.External(actions, target)
      val source = StateOrJunction.Junction(junction)
      val cft = ConstructFlattenedTransition(sma, source)
      cft.transition(transition0)
    }
    val fjtm = sma.flattenedJunctionTransitionMap + (exprNode -> transition)
    Right(sma.copy(flattenedJunctionTransitionMap = fjtm))
  }

}
