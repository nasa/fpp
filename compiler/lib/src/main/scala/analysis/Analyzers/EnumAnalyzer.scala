package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze enum definitions */
trait EnumAnalyzer extends Analyzer {

  override def defEnumAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    visitList(a, data.constants, defEnumConstantAnnotatedNode)
  }

  def defEnumConstantAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnumConstant]]): Result = 
    default(a)

}
