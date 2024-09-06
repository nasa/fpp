package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Checks for junction cycles */
object CheckJunctionCycles {

  def stateMachineAnalysis(sma: StateMachineAnalysis): Result.Result[Unit] = {
    val nodes = sma.transitionGraph.arcMap.keys.toList
    for {
      _ <- Result.foldLeft (nodes) (State(sma)) {
        case (s, TransitionGraph.Node(StateOrJunction.Junction(j))) =>
          visit(s.clearPath, j)
        case (s, _) => Right(s)
      }
    } yield ()
  }

  private case class State(
    sma: StateMachineAnalysis,
    visited: Set[StateMachineSymbol.Junction] = Set(),
    pathSet: Set[StateMachineSymbol.Junction] = Set(),
    pathList: List[TransitionGraph.Arc] = Nil
  ) {
    def clearPath: State = this.copy(pathSet = Set(), pathList = Nil)
  }

  private def visit(s: State, j: StateMachineSymbol.Junction):
  Result.Result[State] =
    if s.pathSet.contains(j)
    then {
      val loc = Locations.get(j.getNodeId)
      val msg = (
        "encountered a junction cycle:" ::
        s.pathList.reverse.map(_.showTransition)
      ).mkString("\n  ")
      Left(SemanticError.StateMachine.JunctionCycle(loc, msg))
    }
    else {
      val s1 = s.copy(pathSet = s.pathSet + j)
      val soj = StateOrJunction.Junction(j)
      val node = TransitionGraph.Node(soj)
      val nodes = s.sma.transitionGraph.arcMap(node).toList
      for {
        s <- Result.foldLeft (nodes) (s1) (
          (s, a) => a.getEndNode.soj match {
            case StateOrJunction.Junction(j1) => {
              val s2 = s.copy(pathList = a :: s.pathList)
              visit(s2, j1)
            }
            case _ => Right(s)
          }
        )
      } yield s.copy(visited = s.visited + j)
    }

}
