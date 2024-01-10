package fpp.compiler.analysis.dictionary

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.analysis._

case class ResolvedComponentInstance(
    /** The map from resolved ID to command */
    resolvedIdCommandMap: Map[BigInt, Command] = Map(),
    /** The map from resolved ID to telemetry channel */
    resolvedIdChannelMap: Map[BigInt, TlmChannel] = Map(),
    /** The map from resolved ID to event */
    resolvedIdEventMap: Map[BigInt, Event] = Map(),
    /** The map from resolved ID to parameter */
    resolvedIdParamMap: Map[BigInt, Param] = Map()
)

/** Dictionary data structure */
case class Dictionary(
    /** A set of type symbols used in the topology */
    typeSymbolSet: Set[Symbol] = Set(),
    /** The map from resolved ID to command */
    commandEntryMap: Map[BigInt, Command] = Map(),
    /** The map from resolved ID to telemetry channel */
    channelEntryMap: Map[BigInt, TlmChannel] = Map(),
    /** The map from resolved ID to event */
    eventEntryMap: Map[BigInt, Event] = Map(),
    /** The map from resolved ID to parameter */
    paramEntryMap: Map[BigInt, Param] = Map(),
    /** The map from resolved ID to record */
    recordEntryMap: Map[BigInt, Record] = Map()
    // /** The map from resolved ID to container */
    // containerEntryMap: Map[BigInt, Semantics.Container] = Map() // "Container" not found?
) {
    /** Given an analysis, returns all used symbols within commands, telemetry channels, parameters, and events */
    def getUsedSymbols(analysis: Analysis, topology: Topology): Set[Symbol] = {
        val symbolSetList  = for (componentInstance, _) <- topology.instanceMap yield {
            val component = componentInstance.component
            val commandSymbolSet = for (_, command) <- component.commandMap yield {
                command match {
                    case fpp.compiler.analysis.Command.NonParam(aNode, kind) => {
                        val Right(a) = UsedSymbols.specCommandAnnotatedNode(analysis, aNode)
                        a.usedSymbolSet
                    }
                    case fpp.compiler.analysis.Command.Param(aNode, kind) => Set()
                }
            }
            val eventSymbolSet = for (_, event) <- component.eventMap yield {
                val Right(a) = UsedSymbols.specEventAnnotatedNode(analysis, event.aNode)
                a.usedSymbolSet
            }
            val tlmChannelSymbolSet = for (_, channel) <- component.tlmChannelMap yield {
                val Right(a) = UsedSymbols.specTlmChannelAnnotatedNode(analysis, channel.aNode)
                a.usedSymbolSet
            }
            val paramSymbolSet = for (_, param) <- component.paramMap yield {
                val Right(a) = UsedSymbols.specParamAnnotatedNode(analysis, param.aNode)
                a.usedSymbolSet
            }
            val combined = commandSymbolSet ++ eventSymbolSet ++ tlmChannelSymbolSet ++ paramSymbolSet
            combined.foldLeft(Set[Symbol]()) ((acc, elem) => acc ++ elem)

        }
       // merge list of sets into a single set and return
       symbolSetList.foldLeft(Set[Symbol]()) ((acc, elem) => acc ++ elem)
    }

    /** Given an map and base IDs, returns a mapping of resolved IDs to map values */
    def resolveIds[T](currentMap: Map[BigInt, T], baseId: BigInt): Map[BigInt, T] = {
        currentMap.map((id, elem) => baseId + id -> elem)
    }

    def buildDictionary(analysis: Analysis, topology: Topology): Dictionary = {
        val instances = topology.instanceMap.keys
        Dictionary(
            typeSymbolSet=getUsedSymbols(analysis, topology),
            commandEntryMap=instances.foldLeft(Map[BigInt, Command]()) ((acc, inst) => acc ++ resolveIds(inst.component.commandMap, inst.baseId)),
            channelEntryMap=instances.foldLeft(Map[BigInt, TlmChannel]()) ((acc, inst) => acc ++ resolveIds(inst.component.tlmChannelMap, inst.baseId)),
            eventEntryMap=instances.foldLeft(Map[BigInt, Event]()) ((acc, inst) => acc ++ resolveIds(inst.component.eventMap, inst.baseId)),
            paramEntryMap=instances.foldLeft(Map[BigInt, Param]()) ((acc, inst) => acc ++ resolveIds(inst.component.paramMap, inst.baseId))
        )
    }
}