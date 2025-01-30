package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP telemetry packet group */
final case class TlmPacketGroup(
  /** The annotated AST node */
  aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacketGroup]],
  /** The map from packet IDs to packets */
  packetMap: Map[TlmPacket.Id, TlmPacket] = Map(),
  /** The next default parameter ID */
  defaultParamId: TlmPacket.Id = 0,
  /** The set of omitted channel IDs */
  omitted: Set[TlmChannel.Id] = Set()
) {

  /** Gets the name of the packet */
  def getName = aNode._2.data.name

  /** Gets the location of the packet specifier */
  def getLoc: Location = Locations.get(aNode._2.id)

}

object TlmPacketGroup {

  // TODO

}
