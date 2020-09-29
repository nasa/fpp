package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.transform._

/** Check semantics for a list of translation units */
object CheckSemantics {

  def tuList(a: Analysis, tul: List[Ast.TransUnit]): Result.Result[Analysis] = {
    for {
      a_tul <- ResolveSpecInclude.transformList(
        a,
        tul, 
        ResolveSpecInclude.transUnit
      )
      a <- Right(a_tul._1)
      tul <- Right(a_tul._2)
      a <- EnterSymbols.visitList(a, tul, EnterSymbols.transUnit)
      a <- CheckUses.visitList(a, tul, CheckUses.transUnit)
      _ <- CheckUseDefCycles.visitList(a, tul, CheckUseDefCycles.transUnit)
      a <- CheckTypeUses.visitList(a, tul, CheckTypeUses.transUnit)
      a <- CheckExprTypes.visitList(a, tul, CheckExprTypes.transUnit)
      a <- EvalImpliedEnumConsts.visitList(a, tul, EvalImpliedEnumConsts.transUnit)
      a <- EvalConstantExprs.visitList(a, tul, EvalConstantExprs.transUnit)
      a <- FinalizeTypeDefs.visitList(a, tul, FinalizeTypeDefs.transUnit)
      a <- CheckPortDefs.visitList(a, tul, CheckPortDefs.transUnit)
      // TODO: Check component definitions
      // TODO: Check component instance definitions
      // TODO: Check topology definitions
      // TODO: Check init specifiers
      a <- BuildSpecLocMap.visitList(a, tul, BuildSpecLocMap.transUnit)
      a <- CheckSpecLocs.visitList(a, tul, CheckSpecLocs.transUnit)
    }
    yield a
  }

}
