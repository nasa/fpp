package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP telemetry packet set */
final case class TlmPacketSet(
  /** The annotated AST node */
  aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacketSet]],
  /** The map from packet IDs to packets */
  packetMap: Map[TlmPacket.Id, TlmPacket] = Map(),
  /** The next default packet ID */
  defaultPacketId: TlmPacket.Id = 0,
  /** The set of omitted channel IDs */
  omittedIdSet: Set[TlmChannel.Id] = Set(),
  /** The map from each omitted channel ID to a location
   *  where the channel is marked as omitted.
   *  If the channel appears more than once in the omitted list in
   *  the source model, the map contains the last location. */
  omittedLocationMap: Map[TlmChannel.Id, Location] = Map()
) {

  /** Gets the name of the packet */
  def getName = aNode._2.data.name

  /** Gets the location of the packet specifier */
  def getLoc: Location = Locations.get(aNode._2.id)

  /** Add a telemetry packet to the set */
  def addPacket(
    idOpt: Option[Event.Id],
    packet: TlmPacket
  ): Result.Result[TlmPacketSet] = {
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

  /** Gets the channels used in the packet set */
  def getUsedIdSet: Set[TlmChannel.Id] =
    packetMap.values.toSet.flatMap(_.memberIdList.toSet)

  /** Gets the used ID location map for the packet set */
  def getUsedIdLocationMap: Map[TlmChannel.Id, Location] =
    packetMap.values.flatMap(_.memberLocationMap).toMap

}

object TlmPacketSet {

  // Computes the omitted channels of the packet set
  private def computeOmittedChannels
    (a: Analysis, d: Dictionary, t: Topology)
    (tpg: TlmPacketSet):
  Result.Result[TlmPacketSet] = {
    val nodeList = tpg.aNode._2.data.omitted
    for {
      idList <- Result.map(
        nodeList,
        TlmChannelIdentifier.getNumericIdForNode (a, d, t)
      )
    }
    yield {
      val locs = nodeList.map(node => Locations.get(node.id))
      val locationMap = idList.zip(locs).toMap
      tpg.copy(
        omittedIdSet = idList.toSet,
        omittedLocationMap = locationMap
      )
    }
  }

  // Checks that each channel is either used or omitted, but not both
  private def checkChannelUsage
    (a: Analysis, d: Dictionary, t: Topology)
    (tpg: TlmPacketSet):
  Result.Result[Unit] = {
    val usedIdSet = tpg.getUsedIdSet
    val usedIdLocationMap = tpg.getUsedIdLocationMap
    lazy val setName = tpg.getName
    lazy val setLoc = Locations.get(tpg.aNode._2.id)
    def checkIdUsedOrOmitted(id: TlmChannel.Id) =
      if !usedIdSet.contains(id) && !tpg.omittedIdSet.contains(id)
      then {
        val entry = d.tlmChannelEntryMap(id)
        val channelName = entry.getName
        val instanceLoc = t.instanceMap(entry.instance)._2
        val channelLoc = entry.tlmChannel.getLoc
        val msg = s"""|telemetry channel $channelName is neither used nor marked as omitted
                      |
                      |component instance is specified here:
                      |$instanceLoc
                      |
                      |telemetry channel is specified here:
                      |$channelLoc""".stripMargin
        Left(SemanticError.InvalidTlmPacketSet(setLoc, setName, msg))
      }
      else Right(())
    def checkIdNotUsedAndOmitted(id: TlmChannel.Id) =
      if usedIdSet.contains(id) && tpg.omittedIdSet.contains(id)
      then {
        val entry = d.tlmChannelEntryMap(id)
        val channelName = entry.getName
        val usedLoc = usedIdLocationMap(id)
        val omittedLoc = tpg.omittedLocationMap(id)
        val msg = s"""|telemetry channel $channelName is both used and marked omitted
                      |
                      |used here:
                      |$usedLoc
                      |
                      |marked omitted here:
                      |$omittedLoc""".stripMargin
        Left(SemanticError.InvalidTlmPacketSet(setLoc, setName, msg))
      }
      else Right(())
    def checkId(id: TlmChannel.Id) = for {
      _ <- checkIdUsedOrOmitted(id)
      _ <- checkIdNotUsedAndOmitted(id)
    } yield ()
    val ids = d.tlmChannelEntryMap.keys.toList
    Result.foldLeft (ids) (()) { case (_, id) => checkId(id) }
  }

  /** Completes a telemetry packet set definition */
  def complete(a: Analysis, d: Dictionary, t: Topology) (tpg: TlmPacketSet):
  Result.Result[TlmPacketSet] = for {
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
