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
  /** The map from global IDs to event entries */
  eventEntryMap: Map[Event.Id, Dictionary.EventEntry] = Map(),
  /** The map from global IDs to parameter entries */
  paramEntryMap: Map[Param.Id, Dictionary.ParamEntry] = Map(),
  /** The map from global IDs to record entries */
  recordEntryMap: Map[Record.Id, Dictionary.RecordEntry] = Map(),
  /** The map from global IDs to container entries */
  containerEntryMap: Map[Container.Id, Dictionary.ContainerEntry] = Map(),
  // TODO: Telemetry packet group map
)

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

  /** An event entry dictionary entry */
  case class EventEntry(instance: ComponentInstance, event: Event)

}
