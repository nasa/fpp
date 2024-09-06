package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP transition graph */
case class TransitionGraph(
  initialNode: Option[TransitionGraph.Node] = None,
  arcMap: Map[TransitionGraph.Node, Set[TransitionGraph.Arc]] = Map()
) {

  /** Adds a node to the graph */
  def addNode(node: TransitionGraph.Node): TransitionGraph =
    arcMap.get(node) match {
      case Some(_) => this
      case None => this.copy(arcMap = arcMap + (node -> Set()))
    }

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
    def showTransition: String
  }
  object Arc {
    case class Initial(
      startState: StateMachineSymbol.State,
      aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]],
      endNode: Node
    ) extends Arc {
      def getStartNode = Node(StateOrJunction.State(startState))
      def getEndNode = endNode
      def showTransition = {
        val loc = Locations.get(aNode._2.id)
        val endName = endNode.soj.getName
        s"initial transition at ${loc.file}:${loc.pos} to $endName"
      }
    }
    case class State(
      startState: StateMachineSymbol.State,
      aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]],
      endNode: Node
    ) extends Arc {
      def getStartNode = Node(StateOrJunction.State(startState))
      def getEndNode = endNode
      def showTransition = {
        val loc = Locations.get(aNode._2.id)
        val endName = endNode.soj.getName
        s"state transition at ${loc.file}:${loc.pos} to $endName"
      }
    }
    case class Junction(
      startJunction: StateMachineSymbol.Junction,
      aNode: AstNode[Ast.TransitionExpr],
      endNode: Node
    ) extends Arc {
      def getStartNode = Node(StateOrJunction.Junction(startJunction))
      def getEndNode = endNode
      def showTransition = {
        val loc = Locations.get(aNode.id)
        val endName = endNode.soj.getName
        s"junction transition at ${loc.file}:${loc.pos} to $endName"
      }
    }
  }

}
