package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze template expansions members */
trait ModuleTemplateDefAnalyzer extends Analyzer {

  override def defModuleTemplateAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]
  ) =
    for {
      a <- super.defModuleTemplateAnnotatedNode(a, aNode)
      a <- ModuleTemplateDefAnalyzer.visit(this, a, aNode)
    } yield a

}

object ModuleTemplateDefAnalyzer {

  def visit(
    analyzer: ModuleTemplateDefAnalyzer,
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    for {
      a <- Right(a.copy(templateDefId = Some(node.id)))
      a <- analyzer.visitList(a, data.members, analyzer.matchModuleMember)
      a <- Right(a.copy(templateDefId = None))
    } yield a
  }

}
