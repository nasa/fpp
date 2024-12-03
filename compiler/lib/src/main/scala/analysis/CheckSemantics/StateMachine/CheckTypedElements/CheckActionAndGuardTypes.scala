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

  override def choiceTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.Choice
  ): Result = {
    val data = te.aNode._2.data
    for {
      _ <- checkGuardType(sma, te, data.guard)
      _ <- checkActionTypes(sma, te, data.ifTransition.data.actions)
      _ <- checkActionTypes(sma, te, data.elseTransition.data.actions)
    } yield sma
  }

  override def stateEntryTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateEntry
  ): Result = {
    val actions = te.aNode._2.data.actions
    checkActionTypes(sma, te, actions)
  }

  override def stateExitTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateExit
  ): Result = {
    val actions = te.aNode._2.data.actions
    checkActionTypes(sma, te, actions)
  }

  override def stateTransitionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateTransition
  ): Result = {
    val data = te.aNode._2.data
    for {
      _ <- data.guard.map(checkGuardType(sma, te, _)).getOrElse(Right(sma))
      _ <- data.transitionOrDo match {
        case Ast.TransitionOrDo.Transition(transition) =>
          checkActionTypes(sma, te, transition.data.actions)
        case Ast.TransitionOrDo.Do(actions) =>
          checkActionTypes(sma, te, actions)
      }
    } yield sma
  }

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
  private def checkGuardType(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement,
    guard: AstNode[Ast.Ident]
  ): Result.Result[StateMachineAnalysis] = {
    val siteKind = "guard"
    def getTypeOption(sym: StateMachineSymbol): Option[Type] = {
      val guardSym @ StateMachineSymbol.Guard(_) = sym
      guardSym.node._2.data.typeName.map(tn => sma.a.typeMap(tn.id))
    }
    checkCallSiteTypes(sma, te, List(guard), siteKind, getTypeOption)
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
