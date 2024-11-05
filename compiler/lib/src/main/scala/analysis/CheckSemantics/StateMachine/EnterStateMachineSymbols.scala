package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Enter state machine symbols into their scopes */
object EnterStateMachineSymbols
  extends StateMachineAnalysisVisitor
  with StateAnalyzer
{

  override def defActionAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefAction]]
  ) = visitNode(sma, aNode, StateMachineSymbol.Action(_), List(StateMachineNameGroup.Action))

  override def defGuardAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefGuard]]
  ) = visitNode(sma, aNode, StateMachineSymbol.Guard(_), List(StateMachineNameGroup.Guard))

  override def defChoiceAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefChoice]]
  ) = visitNode(sma, aNode, StateMachineSymbol.Choice(_), List(StateMachineNameGroup.State))

  override def defSignalAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefSignal]]
  ) = visitNode(sma, aNode, StateMachineSymbol.Signal(_), List(StateMachineNameGroup.Signal))

  override def defStateAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val parentState = sma.parentState
    val symbol = StateMachineSymbol.State(aNode)
    for {
      sma <- visitNode(sma, aNode, StateMachineSymbol.State(_), List(StateMachineNameGroup.State))
      sma <- {
        val scope = StateMachineScope.empty
        val nestedScope = sma.nestedScope.push(scope)
        val sma1 = sma.copy(nestedScope = nestedScope, parentState = Some(symbol))
        super.defStateAnnotatedNode(sma1, aNode)
      }
    }
    yield {
      val scope = sma.nestedScope.innerScope
      val symbolScopeMap = sma.symbolScopeMap + (symbol -> scope)
      sma.copy(
        nestedScope = sma.nestedScope.pop,
        parentState = parentState,
        symbolScopeMap = symbolScopeMap
      )
    }
  }

  private def updateMap(
    sma: StateMachineAnalysis,
    s: StateMachineSymbol
  ): StateMachineAnalysis = {
    val parentStateMap = sma.parentState.fold (sma.parentStateMap) (ps =>
      sma.parentStateMap + (s -> ps)
    )
    sma.copy(parentStateMap = parentStateMap)
  }

  private def visitNode[T](
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[T]],
    symbolConstructor: Ast.Annotated[AstNode[T]] => StateMachineSymbol,
    nameGroups: List[StateMachineNameGroup]
  ): Result.Result[StateMachineAnalysis] = {
    val symbol = symbolConstructor(aNode)
    val name = symbol.getUnqualifiedName
    for {
      nestedScope <- Result.foldLeft (nameGroups) (sma.nestedScope) (
         (ns,ng) => ns.put (ng) (name, symbol)
      )
    }
    yield updateMap(sma, symbol).copy(nestedScope = nestedScope)
  }

}
