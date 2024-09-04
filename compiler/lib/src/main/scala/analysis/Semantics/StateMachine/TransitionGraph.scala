package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP transition graph */
case class TransitionGraph(
  map: Map[TransitionGraph.InitialNode, Set[TransitionGraph.Arc]]
) {

  /** Adds an arc to the graph */
  def addArc(arc: TransitionGraph.Arc): TransitionGraph = {
    val initialNode = arc.initialNode
    val arcs = map.get(initialNode).getOrElse(Set()) + arc
    this.copy(map = map + (initialNode -> arcs))
  }

}

object TransitionGraph {

  sealed trait InitialNode
  object InitialNode {
    final case class InitialTransition(
      aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]]
    ) extends InitialNode
    final case class StateOrJunction(soj: StateOrJunction)
  }

  case class TerminalNode(soj: StateOrJunction)

  case class Arc(
    initialNode: InitialNode,
    transitionExpr: Ast.TransitionExpr,
    terminalnalNode: TerminalNode
  )

}
