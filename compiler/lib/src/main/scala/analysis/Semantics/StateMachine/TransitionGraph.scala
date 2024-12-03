package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP transition graph */
case class TransitionGraph(
  initialNode: Option[TransitionGraph.Node] = None,
  arcMap: TransitionGraph.ArcMap = Map()
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

  /** Adds a reverse arc to the graph */
  def addReverseArc(arc: TransitionGraph.Arc): TransitionGraph = {
    val endNode = arc.getEndNode
    val arcs = arcMap.get(endNode).getOrElse(Set()) + arc
    this.copy(arcMap = arcMap + (endNode -> arcs))
  }

  /** Gets the reverse of this transition graph */
  def getReverseGraph: TransitionGraph = {
    val tg = TransitionGraph(
      initialNode,
      initialNode.map(node => Map(node -> Set())).getOrElse(Map())
    )
    arcMap.values.foldLeft (tg) (
      (tg, arcs) => arcs.foldLeft (tg) ((tg, a) => tg.addReverseArc(a))
    )
  }

}

object TransitionGraph {

  type ArcMap = Map[Node, Set[Arc]]

  case class Node(soc: StateOrChoice)

  sealed trait Arc {
    def getStartNode: Node
    def getEndNode: Node
    def getTypedElement: StateMachineTypedElement
    def showKind: String
    def showTransition: String
  }
  object Arc {
    case class Initial(
      startState: StateMachineSymbol.State,
      aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]],
      endNode: Node
    ) extends Arc {
      def getStartNode = Node(StateOrChoice.State(startState))
      def getEndNode = endNode
      def getTypedElement = StateMachineTypedElement.InitialTransition(aNode)
      def showKind = "initial transition"
      def showTransition = {
        val loc = Locations.get(aNode._2.id)
        val endName = endNode.soc.getName
        s"$showKind at ${loc.file}:${loc.pos} to $endName"
      }
    }
    case class State(
      startState: StateMachineSymbol.State,
      aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]],
      endNode: Node
    ) extends Arc {
      def getStartNode = Node(StateOrChoice.State(startState))
      def getEndNode = endNode
      def getTypedElement = StateMachineTypedElement.StateTransition(aNode)
      def showKind = "state transition"
      def showTransition = {
        val loc = Locations.get(aNode._2.id)
        val endName = endNode.soc.getName
        s"$showKind at ${loc.file}:${loc.pos} to $endName"
      }
    }
    case class Choice(
      startChoice: StateMachineSymbol.Choice,
      aNode: AstNode[Ast.TransitionExpr],
      endNode: Node
    ) extends Arc {
      def getStartNode = Node(StateOrChoice.Choice(startChoice))
      def getEndNode = endNode
      def getTypedElement = StateMachineTypedElement.Choice(startChoice.node)
      def showKind = "choice transition"
      def showTransition = {
        val loc = Locations.get(aNode.id)
        val endName = endNode.soc.getName
        s"$showKind at ${loc.file}:${loc.pos} to $endName"
      }
    }
  }

}
