package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute the flattened junction transition map */
object ComputeFlattenedChoiceTransitionMap
  extends TransitionExprAnalyzer
{

  override def junctionTransitionExpr(
    sma: StateMachineAnalysis,
    junction: StateMachineSymbol.Choice,
    exprNode: AstNode[Ast.TransitionExpr]
  ): Result = {
    val transition = {
      val actions = exprNode.data.actions.map(sma.getActionSymbol)
      val target = sma.getStateOrChoice(exprNode.data.target)
      val transition0 = Transition.External(actions, target)
      val source = StateOrChoice.Choice(junction)
      val cft = ConstructFlattenedTransition(sma, source)
      cft.transition(transition0)
    }
    val fjtm = sma.flattenedChoiceTransitionMap + (exprNode -> transition)
    Right(sma.copy(flattenedChoiceTransitionMap = fjtm))
  }

}
