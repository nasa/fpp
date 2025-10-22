package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check topology definitions */
object CheckTopologyDefs
  extends Analyzer
  with ModuleAnalyzer
  with TopologyAnalyzer
{

  override def defTopologyAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val symbol = Symbol.Topology(aNode)
    a.topologyMap.get(symbol) match {
      case None =>
        // Topology is not in the map: visit it
        val a1 = a.copy(topology = Some(Topology(aNode, a.getQualifiedName(symbol))))
        for {
          // Visit topology members and compute unresolved top
          a <- super.defTopologyAnnotatedNode(a1, aNode)
          top <- Right(a.topology.get)
          a <- {
            // Resolve topologies directly imported by top, updating a
            val tops = top.directTopologies.toList
            Result.foldLeft (tops) (a) ((a, tl) => {
              defTopologyAnnotatedNode(a, tl._1.node)
            })
          }
          // Use the updated analysis to resolve top
          top <- ResolveTopology.resolve(a, top)
        }
        yield a.copy(topologyMap = a.topologyMap + (symbol -> top))
      case _ => {
        // Topology is already in the map: nothing to do
        Right(a)
      }
    }
  }

  override def specTopPortAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTopPort]]
  ) = {
    Right(a.copy(topology = Some(a.topology.get.addPortNode(aNode))))
  }

  override def specInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInstance]]
  ) = {
    val node = aNode._2
    val instanceNode = node.data.instance
    for {
      symbol <- a.getInterfaceInstanceSymbol(instanceNode.id)
      topology <- a.topology.get.addInstanceSymbol(symbol, Locations.get(node.id))
    }
    yield a.copy(topology = Some(topology))
  }

  override def specConnectionGraphAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecConnectionGraph]]
  ) = {
    for {
      topology <- aNode._2.data match {
        case direct: Ast.SpecConnectionGraph.Direct =>
          Result.foldLeft (direct.connections) (a.topology.get) ((t, ast) =>
            for (c <- Connection.fromAst(a, ast))
              yield t.addLocalConnection(direct.name, c)
          )
        case pattern: Ast.SpecConnectionGraph.Pattern => 
          for {
            p <- ConnectionPattern.fromSpecConnectionGraph(a, aNode, pattern)
            t <- a.topology.get.addPattern(pattern.kind, p)
          } yield t
      }
    } yield a.copy(topology = Some(topology))
  }

}
