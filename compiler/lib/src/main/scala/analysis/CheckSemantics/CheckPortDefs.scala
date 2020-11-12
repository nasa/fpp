package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check port definitions */
object CheckPortDefs
  extends Analyzer 
  with ModuleAnalyzer
{

  override def defPortAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefPort]]) = {
    val (_, node, _) = aNode
    val data = node.data
    for (_ <- Analysis.checkForDuplicateParameter(data.params))
      yield a
  }

}
