package fpp.compiler.analysis

import fpp.compiler.util._

/** Dictionary data structure */
case class Dictionary(
 /** A set of symbols used in the topology */
 usedSymbolSet: Set[Symbol] = Set(),
 /** The map from resolved IDs to command entries */
 commandEntryMap: Map[BigInt, Dictionary.CommandEntry] = Map(),
 /** The map from resolved IDs to telemetry channel entries */
 tlmChannelEntryMap: Map[BigInt, Dictionary.TlmChannelEntry] = Map(),
 /** The map from resolved IDs to event entries */
 eventEntryMap: Map[BigInt, Dictionary.EventEntry] = Map(),
 /** The map from resolved IDs to parameter entries */
 paramEntryMap: Map[BigInt, Dictionary.ParamEntry] = Map(),
 /** The map from resolved IDs to record entries */
 recordEntryMap: Map[BigInt, Dictionary.RecordEntry] = Map(),
 /** The map from resolved IDs to container entries */
 containerEntryMap: Map[BigInt, Dictionary.ContainerEntry] = Map(),
 // TODO: Telemetry packets
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

  /** Constructs the initial dictionary (no telemetry packets) */
  def initial(a: Analysis, t: Topology) =
    Dictionary(
      Set(),
      getCommandEntryMap(t),
      getTlmChannelEntryMap(t),
      getEventEntryMap(t),
      getParamEntryMap(t),
      getRecordEntryMap(t),
      getContainerEntryMap(t)
    )

  private val getCommandEntryMap =
    getEntryMap (_.commandMap) (CommandEntry.apply)

  private val getTlmChannelEntryMap =
    getEntryMap (_.tlmChannelMap) (TlmChannelEntry.apply)

  private val getEventEntryMap =
    getEntryMap (_.eventMap) (EventEntry.apply)

  private val getParamEntryMap =
    getEntryMap (_.paramMap) (ParamEntry.apply)

  private val getRecordEntryMap =
    getEntryMap (_.recordMap) (RecordEntry.apply)

  private val getContainerEntryMap =
    getEntryMap (_.containerMap) (ContainerEntry.apply)

  private def getEntryMap[Specifier, Entry]
    (getSpecMap: Component => Map[BigInt, Specifier])
    (constructEntry: (ComponentInstance, Specifier) => Entry)
    (t: Topology)
  = {
    def addEntriesForInstance(
      entryMap: Map[BigInt, Entry],
      ci: ComponentInstance,
    ) = {
      val m = getSpecMap(ci.component)
      m.foldLeft(entryMap) (addEntry(constructEntry, ci))
    }
    t.instanceMap.keys.foldLeft (Map[BigInt, Entry]()) (addEntriesForInstance),
  }

  private def addEntry[Specifier, Entry](
    constructEntry: (ComponentInstance, Specifier) => Entry,
    ci: ComponentInstance
  ) = {
    (m: Map[BigInt, Entry], idSpecifierPair: (BigInt, Specifier)) =>  {
      val (localId, s) = idSpecifierPair
      val id = ci.baseId + localId
      val entry = constructEntry(ci, s)
      m + (id -> entry)
    }
  }

}
