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
  /** The set of omitted channel IDs */
  omittedIdSet: Set[TlmChannel.Id] = Set(),
  /** The map from omitted channel IDs to their locations */
  omittedIdLocationMap: Map[TlmChannel.Id, Location] = Map()
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

  /** Gets the channels used in the packet group */
  def getUsedChannelIds: Set[TlmChannel.Id] =
    packetMap.values.toSet.flatMap(_.memberIds.toSet)

  /** Gets the used ID location map for the packet group */
  def getUsedIdLocationMap: Map[TlmChannel.Id, Location] =
    packetMap.values.flatMap(_.memberLocationMap).toMap

}

object TlmPacketGroup {

  /** Computes the omitted channels */
  private def computeOmittedChannels
    (a: Analysis, d: Dictionary, t: Topology)
    (tpg: TlmPacketGroup):
  Result.Result[TlmPacketGroup] = {
    val omittedNodeList = tpg.aNode._2.data.omitted
    for {
      omittedIdList <- Result.map(
        omittedNodeList,
        TlmChannelIdentifier.getNumericIdForNode (a, d, t)
      )
    }
    yield {
      val locs = omittedNodeList.map(node => Locations.get(node.id))
      val locationMap = omittedIdList.zip(locs).toMap
      tpg.copy(
        omittedIdSet = omittedIdList.toSet,
        omittedIdLocationMap = locationMap
      )
    }
  }

  /** Checks that each channel is either used or omitted */
  private def checkChannelUsage
    (a: Analysis, d: Dictionary, t: Topology)
    (tpg: TlmPacketGroup):
  Result.Result[Unit] = {
    // TODO
    Right(())
  }

  /** Completes a telemetry packet group definition */
  def complete(a: Analysis, d: Dictionary, t: Topology) (tpg: TlmPacketGroup):
  Result.Result[TlmPacketGroup] = for {
    _ <- Analysis.checkDictionaryNames(
      tpg.packetMap,
      "packet",
      _.getName,
      _.getLoc
    )
    tpg <- computeOmittedChannels (a, d, t) (tpg)
    _ <- checkChannelUsage (a, d, t) (tpg)
  }
  yield tpg

}
