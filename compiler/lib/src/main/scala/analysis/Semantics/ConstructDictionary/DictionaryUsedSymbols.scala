package fpp.compiler.analysis

import fpp.compiler.util._

/** Fills in the used symbols for a dictionary */
final case class DictionaryUsedSymbols(a: Analysis, t: Topology) {

  def updateUsedSymbols(d: Dictionary): Dictionary =
    d.copy(usedSymbolSet = getUsedSymbolSet)

  private def getUsedSymbolSet: Set[Symbol] =
    val impliedUses = Set.concat(
      a.getImpliedUses(ImpliedUse.Kind.Type, t.aNode._2.id).map(
        iu => a.useDefMap(iu.id)
      ),
      a.getImpliedUses(ImpliedUse.Kind.Constant, t.aNode._2.id).map(
        iu => a.useDefMap(iu.id)
      )
    )
    Set.concat(
      t.instanceMap.keys.toSet.flatMap(getUsedSymbolsForInstance),
      impliedUses,
      impliedUses.flatMap(getUsedSymbolsForImpliedUse),
      a.dictionarySymbolSet
    )

  private def getUsedSymbolsForImpliedUse(s: Symbol) = {
    s match
      case Symbol.Array(aNode) =>
        val Right(updatedAnalysis) = UsedSymbols.defArrayAnnotatedNode(a, aNode)
        UsedSymbols.resolveUses(a, updatedAnalysis.usedSymbolSet)
      case Symbol.AliasType(aNode) =>
        val Right(updatedAnalysis) = UsedSymbols.defAliasTypeAnnotatedNode(a, aNode)
        UsedSymbols.resolveUses(a, updatedAnalysis.usedSymbolSet)
      case Symbol.Struct(aNode) =>
        val Right(updatedAnalysis) = UsedSymbols.defStructAnnotatedNode(a, aNode)
        UsedSymbols.resolveUses(a, updatedAnalysis.usedSymbolSet)
      case Symbol.Constant(aNode) =>
        a.typeMap(aNode._2.id) match
          case Type.Enum(enumNode, _, _) => Set(Symbol.Enum(enumNode))
          case _ => Set()
      case _ => Set()
  }

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
