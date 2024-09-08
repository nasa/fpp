package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** State machine typed element analyzer */
object SmTypedElementAnalyzer
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

  def junctionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.Junction
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
  ): Result = if sma.typeOptionMap.contains(te)
              then Right(sma)
              else te match {
                case it: StateMachineTypedElement.InitialTransition =>
                  initialTransitionTypedElement(sma, it)
                case j: StateMachineTypedElement.Junction =>
                  junctionTypedElement(sma, j)
                case st: StateMachineTypedElement.StateTransition =>
                  stateTransitionTypedElement(sma, st)
              }

  override def defJunctionAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefJunction]]
  ) = visitTypedElement(
    sma,
    StateMachineTypedElement.Junction(aNode)
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
