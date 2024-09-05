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
    val startNode = arc.getStartNode
    val arcs = arcMap.get(startNode).getOrElse(Set()) + arc
    this.copy(arcMap = arcMap + (startNode -> arcs))
  }

}

object TransitionGraph {

  case class Node(soj: StateOrJunction)

  sealed trait Arc {
    def getStartNode: Node
    def getEndNode: Node
  }
  object Arc {
    case class Initial(
      startState: StateMachineSymbol.State,
      aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]],
      endNode: Node
    ) extends Arc {
      def getStartNode = Node(StateOrJunction.State(startState))
      def getEndNode = endNode
    }
    case class State(
      startState: StateMachineSymbol.State,
      aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]],
      endNode: Node
    ) extends Arc {
      def getStartNode = Node(StateOrJunction.State(startState))
      def getEndNode = endNode
    }
    case class Junction(
      startJunction: StateMachineSymbol.Junction,
      aNode: AstNode[Ast.TransitionExpr],
      endNode: Node
    ) extends Arc {
      def getStartNode = Node(StateOrJunction.Junction(startJunction))
      def getEndNode = endNode
    }
  }

}
