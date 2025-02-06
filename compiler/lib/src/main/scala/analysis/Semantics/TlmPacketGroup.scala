package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP telemetry packet group */
final case class TlmPacketGroup(
  /** The annotated AST node */
  aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacketGroup]],
  /** The map from packet IDs to packets */
  packetMap: Map[TlmPacket.Id, TlmPacket] = Map(),
  /** The next default packet ID */
  defaultPacketId: TlmPacket.Id = 0,
  /** The set of used channel IDs */
  usedChannelSet: Set[TlmChannel.Id] = Set(),
  /** The set of omitted channel IDs */
  omittedChannelSet: Set[TlmChannel.Id] = Set()
) {

  /** Gets the name of the packet */
  def getName = aNode._2.data.name

  /** Gets the location of the packet specifier */
  def getLoc: Location = Locations.get(aNode._2.id)

  /** Add a telemetry packet */
  def addPacket(
    idOpt: Option[Event.Id],
    packet: TlmPacket
  ): Result.Result[TlmPacketGroup] = {
    for {
      result <- Analysis.addElementToIdMap(
        packetMap,
        idOpt.getOrElse(defaultPacketId),
        packet,
        _.getLoc
      )
    }
    yield this.copy(
      packetMap = result._1,
      defaultPacketId = result._2
    )
  }

}

object TlmPacketGroup {

  /** Constructs the set of omitted channels */
  private def constructOmittedSet
    (a: Analysis, d: Dictionary, t: Topology)
    (tpg: TlmPacketGroup):
  Result.Result[TlmPacketGroup] = for {
    omitted <- Result.map(
      tpg.aNode._2.data.omitted,
      TlmChannelIdentifier.getNumericIdForNode (a, d, t)
    )
  }
  yield tpg.copy(omittedChannelSet = omitted.toSet)

  /** Checks that each channel is either used or omitted */
  private def checkChannelUsage
    (a: Analysis, d: Dictionary, t: Topology)
    (tpg: TlmPacketGroup):
  Result.Result[Unit] = {
    // TODO
    Right(())
  }

  /** Complete a telemetry packet group definition */
  def complete(a: Analysis, d: Dictionary, t: Topology) (tpg: TlmPacketGroup):
  Result.Result[TlmPacketGroup] = for {
    _ <- Analysis.checkDictionaryNames(
      tpg.packetMap,
      "packet",
      _.getName,
      _.getLoc
    )
    tpg <- constructOmittedSet (a, d, t) (tpg)
    _ <- checkChannelUsage (a, d, t) (tpg)
  }
  yield tpg

}
