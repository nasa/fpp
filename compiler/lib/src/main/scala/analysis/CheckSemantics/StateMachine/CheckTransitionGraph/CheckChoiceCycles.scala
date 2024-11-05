package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Checks for choice cycles */
object CheckChoiceCycles {

  def stateMachineAnalysis(sma: StateMachineAnalysis): Result.Result[Unit] = {
    val nodes = sma.transitionGraph.arcMap.keys.toList
    for {
      _ <- Result.foldLeft (nodes) (State(sma)) {
        case (s, TransitionGraph.Node(StateOrChoice.Choice(j))) =>
          visit(s.clearPath, j)
        case (s, _) => Right(s)
      }
    } yield ()
  }

  private case class State(
    sma: StateMachineAnalysis,
    visited: Set[StateMachineSymbol.Choice] = Set(),
    pathSet: Set[StateMachineSymbol.Choice] = Set(),
    pathList: List[TransitionGraph.Arc] = Nil
  ) {
    def clearPath: State = this.copy(pathSet = Set(), pathList = Nil)
  }

  private def visit(s: State, j: StateMachineSymbol.Choice):
  Result.Result[State] =
    if s.pathSet.contains(j)
    then {
      val loc = Locations.get(j.getNodeId)
      val msg = (
        "encountered a choice cycle:" ::
        s.pathList.reverse.map(_.showTransition)
      ).mkString("\n  ")
      Left(SemanticError.StateMachine.ChoiceCycle(loc, msg))
    }
    else {
      val s1 = s.copy(pathSet = s.pathSet + j)
      val soc = StateOrChoice.Choice(j)
      val node = TransitionGraph.Node(soc)
      val nodes = s.sma.transitionGraph.arcMap(node).toList
      for {
        s <- Result.foldLeft (nodes) (s1) (
          (s, a) => a.getEndNode.soc match {
            case StateOrChoice.Choice(j1) => {
              val s2 = s.copy(pathList = a :: s.pathList)
              visit(s2, j1)
            }
            case _ => Right(s)
          }
        )
      } yield s.copy(visited = s.visited + j)
    }

}
