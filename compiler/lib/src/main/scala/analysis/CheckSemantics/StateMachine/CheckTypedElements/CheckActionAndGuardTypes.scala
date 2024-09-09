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
    checkActionTypes(sma, te, actions)
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

  // Check action types
  private def checkActionTypes(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement,
    actions: List[AstNode[Ast.Ident]]
  ): Result.Result[StateMachineAnalysis] = {
    val siteKind = "action"
    def getTypeOption(sym: StateMachineSymbol): Option[Type] = {
      val actionSym @ StateMachineSymbol.Action(_) = sym
      actionSym.node._2.data.typeName.map(tn => sma.a.typeMap(tn.id))
    }
    checkCallSiteTypes(sma, te, actions, siteKind, getTypeOption)
  }

  // Check guard types
  private def checkGuardTypes(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement,
    guards: List[AstNode[Ast.Ident]]
  ): Result.Result[StateMachineAnalysis] = {
    val siteKind = "guard"
    def getTypeOption(sym: StateMachineSymbol): Option[Type] = {
      val guardSym @ StateMachineSymbol.Guard(_) = sym
      guardSym.node._2.data.typeName.map(tn => sma.a.typeMap(tn.id))
    }
    checkCallSiteTypes(sma, te, guards, siteKind, getTypeOption)
  }

  // Check call site types
  private def checkCallSiteTypes(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement,
    callSites: List[AstNode[Ast.Ident]],
    siteKind: String,
    getTypeOption: (StateMachineSymbol => Option[Type])
  ): Result.Result[StateMachineAnalysis] = {
    val teKind = te.showKind
    val teTo = sma.typeOptionMap(te)
    Result.foldLeft (callSites) (sma) (
      (sma, cs) => {
        val loc = Locations.get(cs.id)
        val sym = sma.useDefMap(cs.id)
        val siteTo = getTypeOption(sym)
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
