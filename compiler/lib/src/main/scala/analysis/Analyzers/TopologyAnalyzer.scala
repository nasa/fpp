package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze components */
trait TopologyAnalyzer extends Analyzer {

  override def defTopologyAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val (_, node1, _) = node
    val Ast.DefTopology(name, members) = node1.getData
    val a1 = a.copy(moduleNameList = name :: a.moduleNameList)
    for { a2 <- visitList(a1, members, matchTopologyMember) }
    yield a2.copy(moduleNameList = a.moduleNameList)
  }

}
