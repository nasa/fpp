package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Compute the flattened state transition map */
object ComputeFlattenedStateTransitionMap
  extends StateMachineAnalysisVisitor
  with StateAnalyzer
{

  override def defStateMachineAnnotatedNodeInternal(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
    members: List[Ast.StateMachineMember]
  ) = super.defStateMachineAnnotatedNodeInternal(
    sma.copy(
      signalTransitionMap = Map(),
      flattenedStateTransitionMap = Map()
    ),
    aNode,
    members
  )

  override def defStateAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefState]]
  ) = {
    val stateTransitions = getStateTransitionSpecifiers(aNode._2.data)
    val savedStm = sma.signalTransitionMap
    val stm = stateTransitions.foldLeft (sma.signalTransitionMap) (
      (stm, sts) => {
        val signal = sma.getSignalSymbol(sts.signal)
        val guardOpt = sts.guard.map(sma.getGuardSymbol)
        val transition = sts.transitionOrDo match {
          case Ast.TransitionOrDo.Transition(transition) =>
            val actions = transition.data.actions.map(sma.getActionSymbol)
            val target = sma.getStateOrJunction(transition.data.target)
            Transition.External(actions, target)
          case Ast.TransitionOrDo.Do(actions) =>
            Transition.Internal(actions.map(sma.getActionSymbol))
        }
        val guardedTransition = Transition.Guarded(guardOpt, transition)
        stm + (signal -> guardedTransition)
      }
    )
    getStateDefs(aNode._2.data) match {
      case Nil =>
        val stm = sma.signalTransitionMap.map(
          (s, gt) => {
            val sourceSym = StateMachineSymbol.State(aNode)
            val source = StateOrJunction.State(sourceSym)
            val cft = ConstructFlattenedTransition(sma, source)
            val transition = cft.transition(gt.transition)
            (s -> Transition.Guarded(gt.guardOpt, transition))
          }
        )
        val state = StateMachineSymbol.State(aNode)
        val fstm = sma.flattenedStateTransitionMap + (state -> stm)
        Right(sma.copy(flattenedStateTransitionMap = fstm))
      case _ =>
        for {
          sma <- Right(sma.copy(signalTransitionMap = stm))
          sma <- super.defStateAnnotatedNode(sma, aNode)
        } yield sma.copy(signalTransitionMap = savedStm)
    }
  }

  private def getStateTransitionSpecifiers(defState: Ast.DefState):
  List[Ast.SpecStateTransition] =
    defState.members.flatMap(
      _.node._2 match {
        case Ast.StateMember.SpecStateTransition(node) => List(node.data)
        case _ => Nil
      }
    )

  private def getStateDefs(defState: Ast.DefState):
  List[Ast.DefState] =
    defState.members.flatMap(
      _.node._2 match {
        case Ast.StateMember.DefState(node) => List(node.data)
        case _ => Nil
      }
    )

}