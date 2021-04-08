package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check init specifiers */
object CheckInitSpecs
  extends Analyzer 
  with ModuleAnalyzer
{

  override def specInitAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInit]]
  ) = {
    default(a)
  }

}
