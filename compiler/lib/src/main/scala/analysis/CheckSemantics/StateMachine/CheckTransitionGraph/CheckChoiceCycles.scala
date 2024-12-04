package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Checks for choice cycles */
object CheckChoiceCycles {

  def stateMachineAnalysis(sma: StateMachineAnalysis): Result.Result[Unit] = {
    val nodes = sma.transitionGraph.arcMap.keys.toList
    for {
      _ <- Result.foldLeft (nodes) (State(sma)) {
        case (s, TransitionGraph.Node(StateOrChoice.Choice(c))) =>
          visit(s.clearPath, c)
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

  private def visit(s: State, c: StateMachineSymbol.Choice):
  Result.Result[State] =
    if s.pathSet.contains(c)
    then {
      val loc = Locations.get(c.getNodeId)
      val msg = (
        "encountered a choice cycle:" ::
        s.pathList.reverse.map(_.showTransition)
      ).mkString("\n  ")
      Left(SemanticError.StateMachine.ChoiceCycle(loc, msg))
    }
    else {
      val s1 = s.copy(pathSet = s.pathSet + c)
      val soc = StateOrChoice.Choice(c)
      val node = TransitionGraph.Node(soc)
      val nodes = s.sma.transitionGraph.arcMap(node).toList
      for {
        s <- Result.foldLeft (nodes) (s1) (
          (s, a) => a.getEndNode.soc match {
            case StateOrChoice.Choice(c1) => {
              val s2 = s.copy(pathList = a :: s.pathList)
              visit(s2, c1)
            }
            case _ => Right(s)
          }
        )
      } yield s.copy(visited = s.visited + c)
    }

}
