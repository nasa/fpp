package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze enum constants */
trait EnumAnalyzer extends Analyzer {

  def defEnumConstantAnnotatedNode(a: Analysis, node: Ast.Annotated[AstNode[Ast.DefEnumConstant]]): Result =
    default(a)

  override def defEnumAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val a1 = a.copy(scopeNameList = data.name :: a.scopeNameList)
    for { a2 <- visitList(a1, data.constants, defEnumConstantAnnotatedNode) }
    yield a2.copy(scopeNameList = a.scopeNameList)
  }

}
