package fpp.compiler.ast

import fpp.compiler.syntax._

/** Visit topology members */
trait TopologyStateTransformer extends AstStateTransformer {

  override def specTlmPacketAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacket]]
  ) = {
    val (pre, node, post) = aNode
    val Ast.SpecTlmPacket(name, id, group, members) = node.data
    for { result <- transformList(s, members, tlmPacketMember) }
    yield {
      val (s1, members1) = result
      val specTlmPacket = Ast.SpecTlmPacket(name, id, group, members1.flatten)
      val node1 = AstNode.create(specTlmPacket, node.id)
      (s1, (pre, node1, post))
    }
  }

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
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val (pre, node, post) = aNode
    val Ast.DefTopology(name, members) = node.data
    for { result <- transformList(s, members, topologyMember) }
    yield {
      val (s1, members1) = result
      val defTopology = Ast.DefTopology(name, members1.flatten)
      val node1 = AstNode.create(defTopology, node.id)
      (s1, (pre, node1, post))
    }
  }

  def tlmPacketMember(s: State, member: Ast.TlmPacketMember): Result[List[Ast.TlmPacketMember]] =
    Right(s, List(member))

  def tlmPacketSetMember(s: State, member: Ast.TlmPacketSetMember): Result[List[Ast.TlmPacketSetMember]] =
    matchTlmPacketSetMember(s, member)

  def topologyMember(s: State, member: Ast.TopologyMember): Result[List[Ast.TopologyMember]] =
    matchTopologyMember(s, member)

}
