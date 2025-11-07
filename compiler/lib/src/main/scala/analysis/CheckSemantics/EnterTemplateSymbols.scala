package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

object EnterTemplateSymbols
  extends Analyzer
  with ModuleAnalyzer
{
  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    (data.members, a.templateExpansionsEntered.contains(node.id)) match {
      case (None, _) => {
        // This template has not been expanded yet, can't do much
        Right(a)
      }
      case (Some(members), true) => {
        // We already entered this expansion
        // Make sure we recursively enter all the symbols
        this.visitList(a, members, this.matchModuleMember)
      }
      case (Some(members), false) => {
        // We have not entered the symbols in this expansion
        val a1 = a.copy(
          templateExpansionsEntered=a.templateExpansionsEntered + node.id
        )

        EnterSymbols.visitList(a1, List(Ast.TransUnit(members)), EnterSymbols.transUnit)
      }
    }
  }
}
