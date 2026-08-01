package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.transform._

/** Expand template expansion specifiers and enter the symbols they produce. */
object ResolveTemplates {
  def transUnit(
    a: Analysis,
    tul: List[Ast.TransUnit]
  ): Result.Result[(Analysis, List[Ast.TransUnit])] = {
    for {
      a <- CheckTemplateUses.visitList(a, tul, CheckTemplateUses.transUnit)
      s_tul <- ExpandTemplates.transformList(a, tul, ExpandTemplates.transUnit)
      tul <- Right(s_tul._2)
      a <- EnterTemplateSymbols.visitList(a, tul, EnterTemplateSymbols.transUnit)
    } yield (a, tul)
  }
}
