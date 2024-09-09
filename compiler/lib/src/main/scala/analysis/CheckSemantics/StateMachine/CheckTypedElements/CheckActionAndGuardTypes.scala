package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check action and guard types */
object CheckActionAndGuardTypes
  extends SmTypedElementAnalyzer
{

  override def initialTransitionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.InitialTransition
  ): Result = {
    val actions = te.aNode._2.data.transition.data.actions
    checkActions(sma, te, actions)
  }

  override def junctionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.Junction
  ): Result =
    // TODO
    default(sma)

  override def stateEntryTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateEntry
  ): Result =
    // TODO
    default(sma)

  override def stateExitTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateExit
  ): Result =
    // TODO
    default(sma)

  override def stateTransitionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateTransition
  ): Result =
    // TODO
    default(sma)

  private def checkActions(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement,
    actions: List[AstNode[Ast.Ident]]
  ): Result.Result[StateMachineAnalysis] = {
    val teKind = te.showKind
    val teTo = sma.typeOptionMap(te)
    val siteKind = "action"
    Result.foldLeft (actions) (sma) (
      (sma, a) => {
        val loc = Locations.get(a.id)
        val sym @ StateMachineSymbol.Action(_) = sma.useDefMap(a.id)
        val siteTo = sym.node._2.data.typeName.map(tn => sma.a.typeMap(tn.id))
        for {
          _ <- sma.convertTypeOptionsAtCallSite(
                 loc,
                 teKind,
                 teTo,
                 siteKind,
                 siteTo
               )
        } yield sma
      }
    )
  }

}
