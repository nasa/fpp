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
  ): Result = default(sma)

  override def junctionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.Junction
  ): Result = default(sma)

  override def stateTransitionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateTransition
  ): Result = default(sma)

}
