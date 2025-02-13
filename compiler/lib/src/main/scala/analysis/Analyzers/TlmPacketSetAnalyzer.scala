package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze telemetry packet set members */
trait TlmPacketSetAnalyzer extends Analyzer {

  override def specTlmPacketSetAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.SpecTlmPacketSet]]
  ) = {
    val (_, node1, _) = node
    val data = node1.data
    visitList(a, data.members, matchTlmPacketSetMember)
  }

}
