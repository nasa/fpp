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
// TODO: do we need to include param set/sav opcodes?
/** Dictionary data structure */
case class Dictionary(
    /** The map from component instance base ID to component */
    baseIdComponentMap: Map[BigInt, Component] = Map(),
    /** The map from resolved ID to command */
    resolvedIdCommandMap: Map[BigInt, Command] = Map(),
    /** The map from resolved ID to telemetry channel */
    resolvedIdChannelMap: Map[BigInt, TlmChannel] = Map(),
    /** The map from resolved ID to event */
    resolvedIdEventMap: Map[BigInt, Event] = Map(),
    /** The map from resolved ID to parameter */
    resolvedIdParamMap: Map[BigInt, Param] = Map()
) {
    /** From an analysis, retrieves all component instances and creates a map
    * from component instance base ID to component, returns the map */
    def buildBaseIdComponentMap(a: Analysis): Map[BigInt, Component] = {
        return a.componentInstanceMap.map((componentInstanceSymbol, componentInstance) =>
            componentInstance.baseId -> componentInstance.component).toMap
    }

    def resolveIds[T](currentMap: Map[BigInt, T], baseId: BigInt): Map[BigInt, T] = {
        return currentMap.map((id, elem) => baseId + id -> elem)
    }
    
    def buildResolvedCommandMap(baseIdComponentMap: Map[BigInt, Component]): Map[BigInt, Command] = {
        return for {
            (baseId, component) <- baseIdComponentMap
            resolved <- resolveIds(component.commandMap, baseId)
        } yield resolved
    }

    def mergeResolvedInstances(inputList: Iterable[ResolvedComponentInstance], outputDict: Dictionary): Dictionary = {
        return inputList match {
            case head::tail => {
                mergeResolvedInstances(tail, outputDict.copy(
                    resolvedIdCommandMap=outputDict.resolvedIdCommandMap ++ head.resolvedIdCommandMap,
                    resolvedIdParamMap=outputDict.resolvedIdParamMap ++ head.resolvedIdParamMap,
                    resolvedIdChannelMap=outputDict.resolvedIdChannelMap ++ head.resolvedIdChannelMap,
                    resolvedIdEventMap=outputDict.resolvedIdEventMap ++ head.resolvedIdEventMap)
                )
            }
            case Nil => outputDict
        }
    }

    def resolveAll(baseIdComponentMap: Map[BigInt, Component]): Iterable[ResolvedComponentInstance] = {
        return for (baseId, component) <- baseIdComponentMap yield {
            ResolvedComponentInstance(
                resolveIds(component.commandMap, baseId), 
                resolveIds(component.tlmChannelMap, baseId),
                resolveIds(component.eventMap, baseId),
                resolveIds(component.paramMap, baseId)
            )
        }
    }

    def buildDictionary(analysis: Analysis): Dictionary = {
        val baseIdComponentMap = buildBaseIdComponentMap(analysis)
        val resolved = resolveAll(baseIdComponentMap)
        val d = mergeResolvedInstances(resolved, this.copy())
        return d
    }
}