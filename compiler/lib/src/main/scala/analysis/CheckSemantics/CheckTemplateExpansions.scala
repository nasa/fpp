package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check template expansions */
object CheckTemplateExpansions
  extends Analyzer
  with ModuleAnalyzer
{

  override def defModuleTemplateAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefModuleTemplate]]
  ) = Right(a)

  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ) = Right(a)

}
