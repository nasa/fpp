package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** State machine typed element analyzer */
trait SmTypedElementAnalyzer
  extends StateMachineAnalysisVisitor
  with StateAnalyzer
{

  // ----------------------------------------------------------------------
  // Interface methods to override
  // Each of these methods is called when a corresponding typed element
  // is visited
  // ----------------------------------------------------------------------

  def initialTransitionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.InitialTransition
  ): Result = default(sma)

  def choiceTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.Choice
  ): Result = default(sma)

  def stateEntryTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateEntry
  ): Result = default(sma)

  def stateExitTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateExit
  ): Result = default(sma)

  def stateTransitionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateTransition
  ): Result = default(sma)

  // ----------------------------------------------------------------------
  // Implementation using StateMachineAnalysisVisitor
  // ----------------------------------------------------------------------

  def visitTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement
  ): Result = te match {
    case it: StateMachineTypedElement.InitialTransition =>
      initialTransitionTypedElement(sma, it)
    case c: StateMachineTypedElement.Choice =>
      choiceTypedElement(sma, c)
    case se: StateMachineTypedElement.StateEntry =>
      stateEntryTypedElement(sma, se)
    case se: StateMachineTypedElement.StateExit =>
      stateExitTypedElement(sma, se)
    case st: StateMachineTypedElement.StateTransition =>
      stateTransitionTypedElement(sma, st)
  }

  override def defChoiceAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefChoice]]
  ) = visitTypedElement(
    sma,
    StateMachineTypedElement.Choice(aNode)
  )

  override def specStateEntryAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateEntry]]
  ) = visitTypedElement(
    sma,
    StateMachineTypedElement.StateEntry(aNode)
  )

  override def specStateExitAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateExit]]
  ) = visitTypedElement(
    sma,
    StateMachineTypedElement.StateExit(aNode)
  )

  override def specInitialTransitionAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]]
  ) = visitTypedElement(
    sma,
    StateMachineTypedElement.InitialTransition(aNode)
  )

  override def specStateTransitionAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]]
  ) = visitTypedElement(
    sma,
    StateMachineTypedElement.StateTransition(aNode)
  )

}
