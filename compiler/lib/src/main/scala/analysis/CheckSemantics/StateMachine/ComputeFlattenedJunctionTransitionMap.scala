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
  ): Result = default(sma)

}
