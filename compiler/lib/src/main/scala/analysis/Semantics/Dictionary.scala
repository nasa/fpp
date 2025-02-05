package fpp.compiler.analysis

import fpp.compiler.util._

/** An FPP dictionary */
final case class Dictionary(
  /** A set of symbols used in the dictionary */
  usedSymbolSet: Set[Symbol] = Set(),
  /** The map from global IDs to command entries */
  commandEntryMap: Map[Command.Opcode, Dictionary.CommandEntry] = Map(),
  /** The map from global IDs to telemetry channel entries */
  tlmChannelEntryMap: Map[TlmChannel.Id, Dictionary.TlmChannelEntry] = Map(),
  /** The reverse telemetry channel map (for packet construction) */
  reverseTlmChannelEntryMap: Map[Dictionary.TlmChannelEntry, TlmChannel.Id] = Map(),
  /** The map from global IDs to event entries */
  eventEntryMap: Map[Event.Id, Dictionary.EventEntry] = Map(),
  /** The map from global IDs to parameter entries */
  paramEntryMap: Map[Param.Id, Dictionary.ParamEntry] = Map(),
  /** The map from global IDs to record entries */
  recordEntryMap: Map[Record.Id, Dictionary.RecordEntry] = Map(),
  /** The map from global IDs to container entries */
  containerEntryMap: Map[Container.Id, Dictionary.ContainerEntry] = Map(),
  /** The map from packet group names to packet groups */
  tlmPacketGroupMap: Map[Name.Unqualified, TlmPacketGroup] = Map()
) {

  /** Updates the reverse tlm channel entry map */
  def updateReverseTlmChannelEntryMap = {
    val m = Map[Dictionary.TlmChannelEntry, TlmChannel.Id]()
    val m1 = tlmChannelEntryMap.foldLeft (m) {
      case (m, (id, entry)) => m + (entry -> id)
    }
    this.copy(reverseTlmChannelEntryMap = m1)
  }

  /** Finds the numeric ID for a telemetry channel identifier */
  def findNumericIdForChannel (t: Topology) (channelId: TlmChannelIdentifier):
  Result.Result[TlmChannel.Id] = {
    val entry = Dictionary.TlmChannelEntry.fromTlmChannelIdentifier(channelId)
    this.reverseTlmChannelEntryMap.get(entry) match {
      case Some(id) => Right(id)
      case None => Left(
        SemanticError.ChannelNotInDictionary(
          channelId.getLoc,
          channelId.getQualifiedName.toString,
          t.getName
        )
      )
    }
  }

  /** Adds a telemetry channel packet group to the packet group map */
  def addTlmPacketGroup(group: TlmPacketGroup):
  Result.Result[Dictionary] = {
    val name = group.getName
    tlmPacketGroupMap.get(name) match {
      case Some(prevGroup) =>
        val loc = group.getLoc
        val prevLoc = prevGroup.getLoc
        Left(SemanticError.DuplicatePacketGroup(name, loc, prevLoc))
      case None =>
        val tlmPacketGroupMap = this.tlmPacketGroupMap + (name -> group)
        val dictionary = this.copy(tlmPacketGroupMap = tlmPacketGroupMap)
        Right(dictionary)
    }
  }

}

object Dictionary {

  /** A command entry in the dictionary */
  case class CommandEntry(instance: ComponentInstance, command: Command)

  /** A container entry in the dictionary */
  case class ContainerEntry(instance: ComponentInstance, container: Container)

  /** A parameter entry in the dictionary */
  case class ParamEntry(instance: ComponentInstance, param: Param)

  /** A record entry in the dictionary */
  case class RecordEntry(instance: ComponentInstance, record: Record)

  /** A telemetry channel entry in the dictionary */
  case class TlmChannelEntry(instance: ComponentInstance, tlmChannel: TlmChannel)

  object TlmChannelEntry {

    def fromTlmChannelIdentifier(identifier: TlmChannelIdentifier) = TlmChannelEntry(
      identifier.componentInstance,
      identifier.tlmChannel
    )

  }

  /** An event entry in the dictionary */
  case class EventEntry(instance: ComponentInstance, event: Event)

}
