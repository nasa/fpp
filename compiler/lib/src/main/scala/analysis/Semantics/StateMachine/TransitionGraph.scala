package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP transition graph */
case class TransitionGraph(
  initialNode: Option[TransitionGraph.Node] = None,
  arcMap: Map[TransitionGraph.Node, Set[TransitionGraph.Arc]] = Map()
) {

  /** Adds an arc to the graph */
  def addArc(arc: TransitionGraph.Arc): TransitionGraph = {
    val startNode = arc.startNode
    val arcs = arcMap.get(startNode).getOrElse(Set()) + arc
    this.copy(arcMap = arcMap + (startNode -> arcs))
  }

}

object TransitionGraph {

  case class Node(soj: StateOrJunction)

  sealed trait TransitionInfo
  object TransitionInfo {
    case class Initial(aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]])
      extends TransitionInfo
    case class State(aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]])
      extends TransitionInfo
    case class Junction(aNode: AstNode[Ast.TransitionExpr])
      extends TransitionInfo
  }

  case class Arc(
    startNode: Node,
    transitionInfo: TransitionInfo,
    endNode: Node
  )

}
