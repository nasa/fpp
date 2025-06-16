package fpp.compiler.analysis

import fpp.compiler.ast.*
import fpp.compiler.util.*

/** Analyze interface members */
trait InterfaceAnalyzer extends Analyzer {

  override def defInterfaceAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefInterface]]
  ) = {
    val (_, node1, _) = node
    val Ast.DefInterface(name, members) = node1.data
    for { a2 <- visitList(a, members, matchInterfaceMember) }
    yield a2
  }

}
