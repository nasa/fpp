package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Checks for choice cycles */
object CheckChoiceCycles {

  def stateMachineAnalysis(sma: StateMachineAnalysis): Result.Result[Unit] = {
    val nodes = sma.transitionGraph.arcMap.keys.toList
    for {
      _ <- Result.foldLeft (nodes) (()) {
        case (s, TransitionGraph.Node(StateOrChoice.Choice(c))) =>
          visit(State(sma, c), c)
        case (s, _) => Right(s)
      }
    } yield ()
  }

  private case class State(
    sma: StateMachineAnalysis,
    rootChoice: StateMachineSymbol.Choice,
    visited: Set[StateMachineSymbol.Choice] = Set(),
    pathList: List[TransitionGraph.Arc] = Nil
  )

  private def visit(s: State, c: StateMachineSymbol.Choice):
  Result.Result[Unit] =
    if s.visited.contains(c) && c == s.rootChoice
    then {
      val loc = Locations.get(c.getNodeId)
      val msg = (
        "encountered a choice cycle:" ::
        s.pathList.reverse.map(_.showTransition)
      ).mkString("\n  ")
      Left(SemanticError.StateMachine.ChoiceCycle(loc, msg))
    }
    else {
      val s1 = s.copy(visited = s.visited + c)
      val soc = StateOrChoice.Choice(c)
      val node = TransitionGraph.Node(soc)
      val nodes = s.sma.transitionGraph.arcMap(node).toList
      for {
        _ <- Result.foldLeft (nodes) (()) (
          (_, a) => a.getEndNode.soc match {
            case StateOrChoice.Choice(c1) => {
              val s2 = s1.copy(pathList = a :: s.pathList)
              visit(s2, c1)
            }
            case _ => Right(())
          }
        )
      } yield ()
    }

}
