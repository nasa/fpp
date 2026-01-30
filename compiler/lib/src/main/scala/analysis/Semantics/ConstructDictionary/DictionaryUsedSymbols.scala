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
    val ss = Set.concat(
      t.componentInstanceMap.keys.toSet.flatMap(getUsedSymbolsForInstance),
      impliedUses,
      a.dictionarySymbolSet
    )
    UsedSymbols.resolveUses(a, ss)

  private def getUsedSymbolsForInstance(ci: ComponentInstance) = {
    val component = ci.component
    val a1 = a.copy(usedSymbolSet = Set())
    val commandSymbols = getUsedSymbolsForSpecifier(
      component.commandMap,
      {
        case Command.NonParam(aNode, _) =>
          UsedSymbols.specCommandAnnotatedNode(a1, aNode)
        case _ => Right(a.copy(usedSymbolSet = Set()))
      }
    )
    val eventSymbols = getUsedSymbolsForSpecifier(
      component.eventMap,
      event => UsedSymbols.specEventAnnotatedNode(a1, event.aNode)
    )
    val tlmChannelSymbols = getUsedSymbolsForSpecifier(
      component.tlmChannelMap,
      channel => UsedSymbols.specTlmChannelAnnotatedNode(a1, channel.aNode)
    )
    val paramSymbols = getUsedSymbolsForSpecifier(
      component.paramMap,
      param => UsedSymbols.specParamAnnotatedNode(a1, param.aNode)
    )
    val recordSymbols = getUsedSymbolsForSpecifier(
      component.recordMap,
      record => UsedSymbols.specRecordAnnotatedNode(a1, record.aNode)
    )
    val containerSymbols = getUsedSymbolsForSpecifier(
      component.containerMap,
      container => UsedSymbols.specContainerAnnotatedNode(a1, container.aNode)
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
    map.values.toSet.flatMap (
      specifier => {
        val Right(a) = usedSymbols(specifier)
        a.usedSymbolSet
      }
    )

}
