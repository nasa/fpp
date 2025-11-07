package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

object CheckTemplateUses
  extends Analyzer
  with ModuleAnalyzer
{
  val helpers = CheckUsesHelpers(
    (a: Analysis) => a.nestedScope,
    (a: Analysis, ns: NestedScope) => a.copy(nestedScope = ns),
    (a: Analysis) => a.symbolScopeMap,
    (a: Analysis) => a.useDefMap,
    (a: Analysis, udm: Map[AstNode.Id, Symbol]) => a.copy(useDefMap = udm)
  )

  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- super.specTemplateExpandAnnotatedNode(a, aNode)
      a <- helpers.visitQualIdentNode (NameGroup.Template) (a, data.template)
    } yield a
  }
}
