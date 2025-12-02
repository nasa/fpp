package fpp.compiler.analysis

import scala.annotation.tailrec

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.transform._

/** Keep expanding and entering symbols from templates until everything is expanded */
object ResolveTemplates {
  private def pass(a: Analysis, tul: List[Ast.TransUnit]) = {
    for {
      a <- CheckTemplateUses.visitList(a, tul, CheckTemplateUses.transUnit)
      s_tul <- ExpandTemplates.transformList(a, tul, ExpandTemplates.transUnit)
      tul <- Right(s_tul._2)
      a <- EnterTemplateSymbols.visitList(a, tul, EnterTemplateSymbols.transUnit)
    } yield (a, tul, s_tul._1)
  }

  @tailrec
  def transUnit(
    a: Analysis,
    tul: List[Ast.TransUnit]
  ): Result.Result[(Analysis, List[Ast.TransUnit])] = {
    // TODO(tumbar) Try to make this a for comprehension
    val res = ResolveTemplates.pass(a, tul)
    res match {
      case Left(err) => Left(err)
      case Right((a, tul, false)) => Right((a, tul))
      case Right((a, tul, true)) => this.transUnit(a, tul)
    }
  }
}