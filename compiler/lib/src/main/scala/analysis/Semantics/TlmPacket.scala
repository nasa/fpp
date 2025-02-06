package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP telemetry packet */
final case class TlmPacket(
  aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacket]],
  level: Int,
  members: Set[TlmChannel.Id]
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
    val members = Set[TlmChannel.Id]()
    for {
      level <- a.getNonnegativeIntValue(data.level.id)
      members <- Result.foldLeft (data.members) (members) (addIdForMember(a, d, t))
    }
    yield TlmPacket(aNode, level, members)
  }

  // Adds the numeric channel ID for a telemetry packet member to a set of channel IDs
  private def addIdForMember
    (a: Analysis, d: Dictionary, t: Topology)
    (channelSet: Set[TlmChannel.Id], tlmPacketMember: Ast.TlmPacketMember):
  Result.Result[Set[TlmChannel.Id]] =
    tlmPacketMember match {
      case Ast.TlmPacketMember.SpecInclude(_) => Right(channelSet)
      case Ast.TlmPacketMember.TlmChannelIdentifier(node) =>
        for { 
          numericId <- TlmChannelIdentifier.getNumericIdForNode (a, d, t) (node) 
        }
        yield channelSet + numericId
    }

}
