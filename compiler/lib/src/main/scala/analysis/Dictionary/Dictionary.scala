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
    recordEntryMap: Map[BigInt, Record] = Map(),
    arraySymbolSet: Set[Symbol.Array] = Set(),
    enumSymbolSet: Set[Symbol.Enum] = Set(),
    structSymbolSet: Set[Symbol.Struct] = Set()
    // /** The map from resolved ID to container */
    // containerEntryMap: Map[BigInt, Semantics.Container] = Map() // "Container" not found?
) {

    def flattenListOfSets(input: Iterable[Set[Symbol]], output: Set[Symbol]): Set[Symbol] = {
        input match {
            case head :: tail => {
                flattenListOfSets(tail, output ++ head)
            }
            case Nil => output
        }
    }

    /** Given a set of symbols, returns subsets corresponding to arrays, enums, and struct type symbols */
    def splitTypeSymbolSet(symbolSet: Set[Symbol], outArray: Set[Symbol.Array], outEnum: Set[Symbol.Enum], outStruct: Set[Symbol.Struct]): (Set[Symbol.Array], Set[Symbol.Enum], Set[Symbol.Struct]) = {
        if (symbolSet.tail.isEmpty) (outArray, outEnum, outStruct) else {
            val (tail, outA, outE, outS) = symbolSet.head match {
                case h: Symbol.Array => (symbolSet.tail, outArray + h, outEnum, outStruct)
                case h: Symbol.Enum => (symbolSet.tail, outArray, outEnum + h, outStruct)
                case h: Symbol.Struct => (symbolSet.tail, outArray, outEnum, outStruct + h)
                case _ => (symbolSet.tail, outArray, outEnum, outStruct)
            }
            splitTypeSymbolSet(tail, outA, outE, outS)
        }
    }

    def populateUsedSymbols(analysis: Analysis, instanceMap: Map[ComponentInstance, (Ast.Visibility, Location)]): Dictionary = {
        val res  = for (componentInstance, _) <- instanceMap yield {
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
            // flatten list of sets to a single set
            flattenListOfSets(commandSymbolSet ++ eventSymbolSet ++ tlmChannelSymbolSet ++ paramSymbolSet, Set())
        }
        // flatten list of sets to a single set
        val symbolSet = flattenListOfSets(res, Set())

        // split set into individual sets consisting of each symbol type (arrays, enums, structs)
        val (arraySymbolSet, enumSymbolSet, structSymbolSet) = splitTypeSymbolSet(symbolSet, Set(), Set(), Set())

        // return updated Dictionary data structure with type symbol set
        this.copy(typeSymbolSet=symbolSet, arraySymbolSet=arraySymbolSet, enumSymbolSet=enumSymbolSet, structSymbolSet=structSymbolSet)
    }

    def resolveIds[T](currentMap: Map[BigInt, T], baseId: BigInt): Map[BigInt, T] = {
        currentMap.map((id, elem) => baseId + id -> elem)
    }

    def mergeResolvedInstances(inputList: Iterable[ResolvedComponentInstance], outputDict: Dictionary): Dictionary = {
        inputList match {
            case head :: tail => {
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

    def resolveAllDictionaryEntries(instanceMap: Map[ComponentInstance, (Ast.Visibility, Location)]): Iterable[ResolvedComponentInstance] = {
        for (componentInstance, _) <- instanceMap yield {
            val component = componentInstance.component
            val baseId = componentInstance.baseId
            ResolvedComponentInstance(
                resolveIds(component.commandMap, baseId), 
                resolveIds(component.tlmChannelMap, baseId),
                resolveIds(component.eventMap, baseId),
                resolveIds(component.paramMap, baseId)
            )
        }
    }

    def buildDictionary(analysis: Analysis): Dictionary = {
        val dictionaryPopulatedSymbols = populateUsedSymbols(analysis, analysis.topologyMap.head._2.instanceMap)
        val resolved = resolveAllDictionaryEntries(analysis.topologyMap.head._2.instanceMap)
        val constructedDictionary = mergeResolvedInstances(resolved, dictionaryPopulatedSymbols)
        constructedDictionary
    }
}