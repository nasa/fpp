package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze topology members */
trait TopologyAnalyzer extends Analyzer {

  override def defTopologyAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val (_, node1, _) = node
    val Ast.DefTopology(name, members) = node1.data
    val a1 = a.copy(scopeNameList = name :: a.scopeNameList)
    for { a2 <- visitList(a1, members, matchTopologyMember) }
    yield a2.copy(scopeNameList = a.scopeNameList)
  }

}
