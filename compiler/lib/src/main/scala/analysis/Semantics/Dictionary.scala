package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.analysis._

/** Case classes for each dictionary entry */
case class CommandEntry(command: Command, componentInstance: ComponentInstance)
case class ParamEntry(param: Param, componentInstance: ComponentInstance)
case class EventEntry(event: Event, componentInstance: ComponentInstance)
case class TlmChannelEntry(tlmChannel: TlmChannel, componentInstance: ComponentInstance)
case class RecordEntry(record: Record, componentInstance: ComponentInstance)
case class ContainerEntry(container: Container, componentInstance: ComponentInstance)

/** Dictionary data structure */
case class Dictionary(
    /** A set of type symbols used in the topology */
    typeSymbolSet: Set[Symbol] = Set(),
    /** The map from resolved ID to command */
    commandEntryMap: Map[BigInt, CommandEntry] = Map(),
    /** The map from resolved ID to telemetry channel */
    tlmChannelEntryMap: Map[BigInt, TlmChannelEntry] = Map(),
    /** The map from resolved ID to event */
    eventEntryMap: Map[BigInt, EventEntry] = Map(),
    /** The map from resolved ID to parameter */
    paramEntryMap: Map[BigInt, ParamEntry] = Map(),
    /** The map from resolved ID to record */
    recordEntryMap: Map[BigInt, RecordEntry] = Map(),
    /** The map from resolved ID to container */
    containerEntryMap: Map[BigInt, ContainerEntry] = Map(),    
)

object Dictionary {

    /** Given an analysis, returns all used symbols within commands, telemetry channels, parameters, and events */
    def getUsedSymbols(analysis: Analysis, topology: Topology): Set[Symbol] = {
        val symbolSetList  = for (componentInstance, _) <- topology.instanceMap yield {
            val component = componentInstance.component
            val commandSymbolSet = for (_, command) <- component.commandMap yield {
                command match {
                    case fpp.compiler.analysis.Command.NonParam(aNode, kind) => {
                        val Right(a) = UsedSymbols.specCommandAnnotatedNode(analysis, aNode)
                        UsedSymbols.resolveUses(analysis, a.usedSymbolSet)
                    }
                    case fpp.compiler.analysis.Command.Param(aNode, kind) => Set()
                }
            }
            val eventSymbolSet = for (_, event) <- component.eventMap yield {
                val Right(a) = UsedSymbols.specEventAnnotatedNode(analysis, event.aNode)
                UsedSymbols.resolveUses(analysis, a.usedSymbolSet)
            }
            val tlmChannelSymbolSet = for (_, channel) <- component.tlmChannelMap yield {
                val Right(a) = UsedSymbols.specTlmChannelAnnotatedNode(analysis, channel.aNode)
                UsedSymbols.resolveUses(analysis, a.usedSymbolSet)
            }
            val paramSymbolSet = for (_, param) <- component.paramMap yield {
                val Right(a) = UsedSymbols.specParamAnnotatedNode(analysis, param.aNode)
                UsedSymbols.resolveUses(analysis, a.usedSymbolSet)
            }
            val recordSymbolSet = for (_, record) <- component.recordMap yield {
                val Right(a) = UsedSymbols.specRecordAnnotatedNode(analysis, record.aNode)
                UsedSymbols.resolveUses(analysis, a.usedSymbolSet)
            }
            val containerSymbolSet = for (_, container) <- component.containerMap yield {
                val Right(a) = UsedSymbols.specContainerAnnotatedNode(analysis, container.aNode)
                UsedSymbols.resolveUses(analysis, a.usedSymbolSet)
            }
            val combined = commandSymbolSet ++ eventSymbolSet ++ tlmChannelSymbolSet ++ paramSymbolSet ++ recordSymbolSet ++ containerSymbolSet
            combined.foldLeft(Set[Symbol]()) ((acc, elem) => acc ++ elem)

        }
       // Merge list of sets into a single set and return
       symbolSetList.foldLeft(Set[Symbol]()) ((acc, elem) => acc ++ elem)
    }

    /** Resolve base identifiers for all entries in the dictionary */
    /** Constructs maps from base identifiers to dictionary entry (ie: CommandEntry, TlmChannelEntry, ParamEntry, etc.) */
    def resolveCommands(componentInstance: ComponentInstance, resultMap: Map[BigInt, CommandEntry]): Map[BigInt, CommandEntry] = {
        componentInstance.component.commandMap.foldLeft(resultMap) ((resultMap, inst) => 
            resultMap + (componentInstance.baseId + inst._1 -> CommandEntry(componentInstance=componentInstance, command=inst._2))
        )
    }

    def resolveTlmChannels(componentInstance: ComponentInstance, resultMap: Map[BigInt, TlmChannelEntry]): Map[BigInt, TlmChannelEntry] = {
        componentInstance.component.tlmChannelMap.foldLeft(resultMap) ((resultMap, inst) => 
            resultMap + (componentInstance.baseId + inst._1 -> TlmChannelEntry(componentInstance=componentInstance, tlmChannel=inst._2))
        )
    }

    def resolveEvents(componentInstance: ComponentInstance, resultMap: Map[BigInt, EventEntry]): Map[BigInt, EventEntry] = {
        componentInstance.component.eventMap.foldLeft(resultMap) ((resultMap, inst) => 
            resultMap + (componentInstance.baseId + inst._1 -> EventEntry(componentInstance=componentInstance, event=inst._2))
        )
    }

    def resolveParams(componentInstance: ComponentInstance, resultMap: Map[BigInt, ParamEntry]): Map[BigInt, ParamEntry] = {
        componentInstance.component.paramMap.foldLeft(resultMap) ((resultMap, inst) => 
            resultMap + (componentInstance.baseId + inst._1 -> ParamEntry(componentInstance=componentInstance, param=inst._2))
        )
    }

    def resolveRecords(componentInstance: ComponentInstance, resultMap: Map[BigInt, RecordEntry]): Map[BigInt, RecordEntry] = {
        componentInstance.component.recordMap.foldLeft(resultMap) ((resultMap, inst) => 
            resultMap + (componentInstance.baseId + inst._1 -> RecordEntry(componentInstance=componentInstance, record=inst._2))
        )
    }

    def resolveContainers(componentInstance: ComponentInstance, resultMap: Map[BigInt, ContainerEntry]): Map[BigInt, ContainerEntry] = {
        componentInstance.component.containerMap.foldLeft(resultMap) ((resultMap, inst) => 
            resultMap + (componentInstance.baseId + inst._1 -> ContainerEntry(componentInstance=componentInstance, container=inst._2))
        )
    }

    /** Constructs dictionary for all component instances in a topology */
    def buildDictionary(analysis: Analysis, topology: Topology): Dictionary = {
        val instances = topology.instanceMap.keys
        Dictionary(
            typeSymbolSet=getUsedSymbols(analysis, topology),
            commandEntryMap=instances.foldLeft(Map[BigInt, CommandEntry]()) ((acc, inst) => resolveCommands(inst, acc)),
            tlmChannelEntryMap=instances.foldLeft(Map[BigInt, TlmChannelEntry]()) ((acc, inst) => resolveTlmChannels(inst, acc)),
            eventEntryMap=instances.foldLeft(Map[BigInt, EventEntry]()) ((acc, inst) => resolveEvents(inst, acc)),
            paramEntryMap=instances.foldLeft(Map[BigInt, ParamEntry]()) ((acc, inst) => resolveParams(inst, acc)),
            recordEntryMap=instances.foldLeft(Map[BigInt, RecordEntry]()) ((acc, inst) => resolveRecords(inst, acc)),
            containerEntryMap=instances.foldLeft(Map[BigInt, ContainerEntry]()) ((acc, inst) => resolveContainers(inst, acc))
        )
    }

}
