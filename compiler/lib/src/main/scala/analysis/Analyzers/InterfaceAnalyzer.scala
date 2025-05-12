package analysis.Analyzers

import fpp.compiler.ast.*
import fpp.compiler.util.*

/** Analyze component members */
trait InterfaceAnalyzer extends Analyzer {

  override def defComponentAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val (_, node1, _) = node
    val Ast.DefComponent(kind, name, members) = node1.data
    val a1 = a.copy(scopeNameList = name :: a.scopeNameList)
    for { a2 <- visitList(a1, members, matchComponentMember) }
    yield a2.copy(scopeNameList = a.scopeNameList)
  }

}
