package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute the type option map */
object ComputeTypeOptionMap
  extends SmTypedElementAnalyzer
{

  override def initialTransitionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.InitialTransition
  ): Result =
    Right(sma.copy(typeOptionMap = sma.typeOptionMap + (te -> None)))

  override def choiceTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.Choice
  ): Result = {
    val sym = StateMachineSymbol.Choice(te.aNode)
    val soc = StateOrChoice.Choice(sym)
    val node = TransitionGraph.Node(soc)
    val arcs = sma.reverseTransitionGraph.arcMap(node).toList
    arcs match {
      case Nil =>
        // Handle the case where no arc comes into J.
        // This happens when J is the initial node in the transition graph.
        Right(sma.copy(typeOptionMap = sma.typeOptionMap + (te -> None)))
      case head :: tail => {
        // Handle the case where at least one arc comes into J.
        val te0 = head.getTypedElement
        for {
          sma0 <- visitTypedElement(sma, te0)
          to0 <- Right(sma0.typeOptionMap(te0))
          smaTeTo <- Result.foldLeft (tail) ((sma0, te0, to0)) {
            case ((sma1, te1, to1), arc) => {
              val name = sym.getUnqualifiedName
              val te2 = arc.getTypedElement
              for {
                sma2 <- visitTypedElement(sma1, te2)
                to2 <- sma2.commonTypeAtChoice(te, te1, to1, te2)
              }
              yield (sma2, te2, to2)
            }
          }
        } yield {
          val (sma, _, to) = smaTeTo
          sma.copy(typeOptionMap = sma.typeOptionMap + (te -> to))
        }
      }
    }
  }

  override def stateEntryTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateEntry
  ): Result =
    Right(sma.copy(typeOptionMap = sma.typeOptionMap + (te -> None)))

  override def stateExitTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateExit
  ): Result =
    Right(sma.copy(typeOptionMap = sma.typeOptionMap + (te -> None)))

  override def stateTransitionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateTransition
  ): Result = {
    val signalId = te.aNode._2.data.signal.id
    val signalSymbol @ StateMachineSymbol.Signal(_) = sma.useDefMap(signalId)
    val signalDef = signalSymbol.node._2.data
    val to = signalDef.typeName.map(node => sma.a.typeMap(node.id))
    Right(
      sma.copy(typeOptionMap = sma.typeOptionMap + (te -> to))
    )
  }

  override def visitTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement
  ): Result = if sma.typeOptionMap.contains(te)
              then Right(sma)
              else super.visitTypedElement(sma, te)

}
