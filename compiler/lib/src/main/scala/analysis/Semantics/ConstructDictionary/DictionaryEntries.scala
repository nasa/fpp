package fpp.compiler.analysis

import fpp.compiler.util._

/** Fills in the dictionary entries */
final case class DictionaryEntries(a: Analysis, t: Topology) {

  def updateEntries(d: Dictionary): Dictionary =
    d.copy(
      commandEntryMap = getCommandEntryMap(t),
      tlmChannelEntryMap = getTlmChannelEntryMap(t),
      eventEntryMap = getEventEntryMap(t),
      paramEntryMap = getParamEntryMap(t),
      recordEntryMap = getRecordEntryMap(t),
      containerEntryMap = getContainerEntryMap(t)
    ).updateReverseTlmChannelEntryMap

  private val getCommandEntryMap =
    getEntryMap (_.commandMap) (Dictionary.CommandEntry.apply)

  private val getTlmChannelEntryMap =
    getEntryMap (_.tlmChannelMap) (Dictionary.TlmChannelEntry.apply)

  private val getEventEntryMap =
    getEntryMap (_.eventMap) (Dictionary.EventEntry.apply)

  private val getParamEntryMap =
    getEntryMap (_.paramMap) (Dictionary.ParamEntry.apply)

  private val getRecordEntryMap =
    getEntryMap (_.recordMap) (Dictionary.RecordEntry.apply)

  private val getContainerEntryMap =
    getEntryMap (_.containerMap) (Dictionary.ContainerEntry.apply)

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
