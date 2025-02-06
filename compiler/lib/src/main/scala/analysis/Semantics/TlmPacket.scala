package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP telemetry packet */
final case class TlmPacket(
  /** The AST node for the packet */
  aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacket]],
  /** The level */
  level: Int,
  /** The identifiers for the member channels */
  memberIds: List[TlmChannel.Id],
  /** The map from member IDs to the locations where the members are defined */
  memberLocationMap: Map[TlmChannel.Id, Location]
) {

  /** Gets the name of the packet */
  def getName = aNode._2.data.name

  /** Gets the location of the packet specifier */
  def getLoc: Location = Locations.get(aNode._2.id)

}

object TlmPacket {

  type Id = BigInt

  /** Creates a telemetry packet from a telemetry packet specifier */
  def fromSpecTlmPacket(
    a: Analysis,
    d: Dictionary,
    t: Topology,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacket]]
  ): Result.Result[TlmPacket] = {
    val node = aNode._2
    val data = node.data
    val members = data.members.collect {
      case Ast.TlmPacketMember.TlmChannelIdentifier(node) => node
    }
    for {
      level <- a.getNonnegativeIntValue(data.level.id)
      memberIds <- Result.map (
        members,
        TlmChannelIdentifier.getNumericIdForNode (a, d, t)
      )
    }
    yield {
      val locs = members.map(node => Locations.get(node.id))
      val locationMap = memberIds.zip(locs).toMap
      TlmPacket(aNode, level, memberIds, locationMap)
    }
  }

}
