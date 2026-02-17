package fpp.compiler.ast

import fpp.compiler.syntax._

/** Visit topology members */
trait TopologyStateTransformer extends AstStateTransformer {

  override def specTlmPacketSetAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacketSet]]
  ) = {
    val (pre, node, post) = aNode
    val Ast.SpecTlmPacketSet(name, members, omitted) = node.data
    for { result <- transformList(s, members, tlmPacketSetMember) }
    yield {
      val (s1, members1) = result
      val defModule = Ast.SpecTlmPacketSet(name, members1.flatten, omitted)
      val node1 = AstNode.create(defModule, node.id)
      (s1, (pre, node1, post))
    }
  }

  override def defTopologyAnnotatedNode(
    s: State,
    node: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefTopology(name, members) = node1.data
    for { result <- transformList(s, members, topologyMember) }
    yield {
      val (s1, members1) = result
      val defTopology = Ast.DefTopology(name, members1.flatten)
      val node2 = AstNode.create(defTopology, node1.id)
      (s1, (pre, node2, post))
    }
  }

  def tlmPacketSetMember(s: State, member: Ast.TlmPacketSetMember): Result[List[Ast.TlmPacketSetMember]] =
    matchTlmPacketSetMember(s, member)

  def topologyMember(s: State, member: Ast.TopologyMember): Result[List[Ast.TopologyMember]] =
    matchTopologyMember(s, member)

}
