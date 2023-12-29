package fpp.compiler.analysis.dictionary

import fpp.compiler.ast._
import fpp.compiler.util._
import fpp.compiler.analysis._

case class ComponentInstanceEntry(baseId: BigInt, component: Component, qualifiedName: String)
case class CommandEntry(resolvedIdentifier: BigInt, command: Command, fullyQualifiedName: String)
case class TlmChannelEntry(resolvedIdentifier: BigInt, channel: TlmChannel, fullyQualifiedName: String)
case class EventEntry(resolvedIdentifier: BigInt, event: Event, fullyQualifiedName: String)
case class ParamEntry(
    resolvedIdentifier: BigInt,
    resolvedSetIdentifier: BigInt,
    resolvedSaveIdentifier: BigInt,
    param: Param, 
    fullyQualifiedName: String
)

case class ResolvedComponentInstance(
    /** The map from resolved ID to command */
    resolvedIdCommandMap: Map[BigInt, CommandEntry] = Map(),
    /** The map from resolved ID to telemetry channel */
    resolvedIdChannelMap: Map[BigInt, TlmChannelEntry] = Map(),
    /** The map from resolved ID to event */
    resolvedIdEventMap: Map[BigInt, EventEntry] = Map(),
    /** The map from resolved ID to parameter */
    resolvedIdParamMap: Map[BigInt, ParamEntry] = Map()
)

/** Dictionary data structure */
case class Dictionary(
    /** The map from resolved ID to dictionary command entry */
    commandEntryMap: Map[BigInt, CommandEntry] = Map(),
    /** The map from resolved ID to dictionary telemetry channel entry */
    channelEntryMap: Map[BigInt, TlmChannelEntry] = Map(),
    /** The map from resolved ID to dictionary event entry */
    eventEntryMap: Map[BigInt, EventEntry] = Map(),
    /** The map from resolved ID to dictionary parameter entry */
    paramEntryMap: Map[BigInt, ParamEntry] = Map()
) {
    /** From an analysis, retrieves all component instances and creates a map
    * from component instance base ID to component, returns the map */
    def buildComponentInstanceList(a: Analysis): List[ComponentInstanceEntry] = {
        a.componentInstanceMap.map((componentInstanceSymbol, componentInstance) => 
            ComponentInstanceEntry(componentInstance.baseId, componentInstance.component, componentInstance.qualifiedName.toString)).toList
    }

    def formatQualifiedName(componentQualifiedIdentifier: String, identifier: String): String =
        componentQualifiedIdentifier + "." + identifier

    def resolveIds[T](currentMap: Map[BigInt, T], baseId: BigInt): Map[BigInt, T] = {
        currentMap.map((id, elem) => baseId + id -> elem)
    }
    
    def buildResolvedCommandMap(baseIdComponentMap: Map[BigInt, Component]): Map[BigInt, Command] = {
        for {
            (baseId, component) <- baseIdComponentMap
            resolved <- resolveIds(component.commandMap, baseId)
        } yield resolved
    }

    def mergeResolvedInstances(inputList: Iterable[ResolvedComponentInstance], outputDict: Dictionary): Dictionary = {
        inputList match {
            case head::tail => {
                mergeResolvedInstances(tail, outputDict.copy(
                    commandEntryMap=outputDict.commandEntryMap ++ head.resolvedIdCommandMap,
                    paramEntryMap=outputDict.paramEntryMap ++ head.resolvedIdParamMap,
                    channelEntryMap=outputDict.channelEntryMap ++ head.resolvedIdChannelMap,
                    eventEntryMap=outputDict.eventEntryMap ++ head.resolvedIdEventMap)
                )
            }
            case Nil => outputDict
        }
    }

    def resolveAll(componentInstanceList: List[ComponentInstanceEntry]): Iterable[ResolvedComponentInstance] = {
        for (entry <- componentInstanceList) yield {
            ResolvedComponentInstance(
                entry.component.commandMap.map((id, elem) => entry.baseId + id -> 
                    CommandEntry(
                        entry.baseId + id,
                        elem, 
                        formatQualifiedName(entry.qualifiedName, elem.getName.toString)
                    )
                ),
                entry.component.tlmChannelMap.map((id, elem) => entry.baseId + id -> 
                    TlmChannelEntry(
                        entry.baseId + id,
                        elem, 
                        formatQualifiedName(entry.qualifiedName, elem.getName.toString)
                    )
                ),
                entry.component.eventMap.map((id, elem) => entry.baseId + id -> 
                    EventEntry(
                        entry.baseId + id,
                        elem, 
                        formatQualifiedName(entry.qualifiedName, elem.getName.toString)
                    )
                ),
                entry.component.paramMap.map((id, elem) => entry.baseId + id -> 
                    ParamEntry(
                        entry.baseId + id, 
                        entry.baseId + elem.setOpcode, 
                        entry.baseId + elem.saveOpcode,
                        elem,
                        formatQualifiedName(entry.qualifiedName, elem.getName.toString)
                    )
                )
            )
        }
    }

    def buildDictionary(analysis: Analysis): Dictionary = {
        val componentInstanceEntryList = buildComponentInstanceList(analysis)
        val resolved = resolveAll(componentInstanceEntryList)
        val d = mergeResolvedInstances(resolved, this.copy())
        d
    }
}