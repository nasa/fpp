package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check typed elements */
object CheckTypedElements
  extends StateMachineAnalysisVisitor
  with StateAnalyzer
{

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

  private def initialTransitionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.InitialTransition
  ): Result =
    Right(sma.copy(typeOptionMap = sma.typeOptionMap + (te -> None)))

  private def junctionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.Junction
  ): Result = {
    val sym = StateMachineSymbol.Junction(te.aNode)
    val soj = StateOrJunction.Junction(sym)
    val node = TransitionGraph.Node(soj)
    val arcs = sma.reverseTransitionGraph.arcMap(node).toList
    val startTe = arcs.head.getTypedElement
    val startTo = sma.typeOptionMap(startTe)
    for {
      smaTeTo <- Result.foldLeft (arcs.tail) ((sma, startTe, startTo)) {
        case ((sma1, te1, to1), arc) => {
          val name = sym.getUnqualifiedName
          val te2 = arc.getTypedElement
          for {
            sma2 <- visitTypedElement(sma1, te1)
            to2 <- sma2.commonTypeAtJunction(name, te1, to1, te2)
          }
          yield (sma2, te2, to2)
        }
      }
    } yield {
      val sma = smaTeTo._1
      val to = smaTeTo._3
      sma.copy(typeOptionMap = sma.typeOptionMap + (te -> to))
    }
  }

  private def stateTransitionTypedElement(
    sma: StateMachineAnalysis,
    te: StateMachineTypedElement.StateTransition
  ): Result = {
    val signalId = te.aNode._2.data.signal.id
    val signalType = sma.a.typeMap(signalId)
    Right(
      sma.copy(typeOptionMap = sma.typeOptionMap + (te -> Some(signalType)))
    )
  }

  private def visitTypedElement(
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

}
