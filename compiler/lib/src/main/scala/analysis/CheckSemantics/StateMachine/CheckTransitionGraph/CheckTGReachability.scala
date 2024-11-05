package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check reachability in the transition graph */
object CheckTGReachability {

  def stateMachineAnalysis(sma: StateMachineAnalysis): Result.Result[Unit] = {
    val nodes = sma.transitionGraph.arcMap.keys.toList
    val reachableNodes = ReachableNodes.compute(sma)
    Result.foldLeft (nodes) (()) (
      (_, node) =>
        if reachableNodes.contains(node)
        then Right(())
        else {
          val loc = Locations.get(node.soc.getSymbol.getNodeId)
          val name = node.soc.getName
          Left(SemanticError.StateMachine.UnreachableNode(name, loc))
        }
    )
  }

  object ReachableNodes {

    def compute(sma: StateMachineAnalysis): Set[TransitionGraph.Node] =
      visit(State(sma), sma.transitionGraph.initialNode.get).visited

    private case class State(
      sma: StateMachineAnalysis,
      visited: Set[TransitionGraph.Node] = Set()
    )

    private def visit(s: State, node: TransitionGraph.Node): State =
      if s.visited.contains(node)
      then s
      else {
        val s1 = s.copy(visited = s.visited + node)
        val arcs = s.sma.transitionGraph.arcMap(node)
        arcs.foldLeft (s1) ((s, arc) => visit(s, arc.getEndNode))
      }

  }

}
