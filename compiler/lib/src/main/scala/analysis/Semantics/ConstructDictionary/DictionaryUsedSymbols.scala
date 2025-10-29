package fpp.compiler.analysis

import fpp.compiler.util._

/** Fills in the used symbols for a dictionary */
final case class DictionaryUsedSymbols(a: Analysis, t: Topology) {

  def updateUsedSymbols(d: Dictionary): Dictionary =
    d.copy(usedSymbolSet = getUsedSymbolSet)

  private def getUsedSymbolSet: Set[Symbol] =
    val kinds = Set(ImpliedUse.Kind.Type, ImpliedUse.Kind.Constant)
    val impliedUses = kinds.flatMap(
      k => a.getImpliedUses(k, t.aNode._2.id).map(iu => a.useDefMap(iu.id))
    )
    Set.concat(
      t.instanceMap.keys.toSet.flatMap(getUsedSymbolsForInstance),
      impliedUses,
      impliedUses.flatMap(resolveUses),
      a.dictionarySymbolSet
    )

  private def resolveUses(s: Symbol) =
    s match
      case _: Symbol.Constant =>
        a.typeMap(s.getNodeId) match
          case t: Type.Enum => Set(Symbol.Enum(t.node))
          case _ => Set()
      case _ => UsedSymbols.resolveUses(a, Set(s))

  private def getUsedSymbolsForInstance(ci: ComponentInstance) = {
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

}
