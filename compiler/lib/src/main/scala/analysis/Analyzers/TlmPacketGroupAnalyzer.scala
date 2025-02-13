package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze telemetry packet group members */
trait TlmPacketGroupAnalyzer extends Analyzer {

  override def specTlmPacketGroupAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.SpecTlmPacketGroup]]
  ) = {
    val (_, node1, _) = node
    val data = node1.data
    visitList(a, data.members, matchTlmPacketGroupMember)
  }

}
