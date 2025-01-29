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
  def initial(a: Analysis, t: Topology) = Dictionary(
    getUsedSymbolSet(a, t),
    getCommandEntryMap(t),
    getTlmChannelEntryMap(t),
    getEventEntryMap(t),
    getParamEntryMap(t),
    getRecordEntryMap(t),
    getContainerEntryMap(t)
  )

  private def getUsedSymbolSet(a: Analysis, t: Topology): Set[Symbol] =
    t.instanceMap.keys.toSet.flatMap(getUsedSymbolsForInstance (a))

  private def getUsedSymbolsForInstance
    (a: Analysis)
    (ci: ComponentInstance)
  = {
    val component = ci.component
    val commandSymbols = getUsedSymbolsForSpecifier(
      component.commandMap,
      {
        case Command.NonParam(aNode, _) =>
          UsedSymbols.specCommandAnnotatedNode(a, aNode)
        case _ => Right(a.copy(usedSymbolSet = Set()))
      }
    )
    val eventSymbols = getUsedSymbolsForSpecifier(
      component.eventMap,
      event => UsedSymbols.specEventAnnotatedNode(a, event.aNode)
    )
    val tlmChannelSymbols = getUsedSymbolsForSpecifier(
      component.tlmChannelMap,
      channel => UsedSymbols.specTlmChannelAnnotatedNode(a, channel.aNode)
    )
    val paramSymbols = getUsedSymbolsForSpecifier(
      component.paramMap,
      param => UsedSymbols.specParamAnnotatedNode(a, param.aNode)
    )
    val recordSymbols = getUsedSymbolsForSpecifier(
      component.recordMap,
      record => UsedSymbols.specRecordAnnotatedNode(a, record.aNode)
    )
    val containerSymbols = getUsedSymbolsForSpecifier(
      component.containerMap,
      container => UsedSymbols.specContainerAnnotatedNode(a, container.aNode)
    )
    Set.concat(
      commandSymbols,
      eventSymbols,
      tlmChannelSymbols,
      paramSymbols,
      recordSymbols,
      containerSymbols
    )
  }

  private def getUsedSymbolsForSpecifier[Specifier](
    map: Map[BigInt, Specifier],
    usedSymbols: Specifier => Result.Result[Analysis]
  ): Set[Symbol] =
    map.values.toSet.flatMap {
      specifier => {
        val Right(a) = usedSymbols(specifier)
        UsedSymbols.resolveUses(a, a.usedSymbolSet)
      }
    }

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
