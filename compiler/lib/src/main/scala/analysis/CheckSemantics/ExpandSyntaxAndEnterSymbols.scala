package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.transform._

/** Perform all syntax expansion required for analysis.
 *  Because template expansion requires def-use analysis,
 *  also enter all symbols into their scopes */
object ExpandSyntaxAndEnterSymbols {
  def tuList(
    a: Analysis,
    tul: List[Ast.TransUnit]
  ): Result.Result[(Analysis, List[Ast.TransUnit])] = {
    for {
      tul <- AddStateEnums.transUnitList(tul)
      a <- EnterSymbols.visitList(a, tul, EnterSymbols.transUnit)
      a <- CheckTemplateUses.visitList(a, tul, CheckTemplateUses.transUnit)
      (_, tul) <- ExpandTemplates.transformList(a, tul, ExpandTemplates.transUnit)
      a <- EnterTemplateExpansionSymbols.visitList(a, tul, EnterTemplateExpansionSymbols.transUnit)
    } yield (a, tul)
  }
}
