package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute the flattened choice transition map */
object ComputeFlattenedChoiceTransitionMap
  extends TransitionExprAnalyzer
{

  override def choiceTransitionExpr(
    sma: StateMachineAnalysis,
    choice: StateMachineSymbol.Choice,
    exprNode: AstNode[Ast.TransitionExpr]
  ): Result = {
    val transition = {
      val actions = exprNode.data.actions.map(sma.getActionSymbol)
      val target = sma.getStateOrChoice(exprNode.data.target)
      val transition0 = Transition.External(actions, target)
      val source = StateOrChoice.Choice(choice)
      val cft = ConstructFlattenedTransition(sma, source)
      cft.transition(transition0)
    }
    val fjtm = sma.flattenedChoiceTransitionMap + (exprNode -> transition)
    Right(sma.copy(flattenedChoiceTransitionMap = fjtm))
  }

}
