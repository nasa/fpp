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
        for {
          // Resolve connections on topologies directly imported into this topology
          a <- {
            // Resolve topologies directly imported by top, updating a
            val top = a.partialTopologyMap(symbol)
            val tops = top.directTopologies.toList
            Result.foldLeft (tops) (a) ((a, tl) => {
              defTopologyAnnotatedNode(a, tl._1.node)
            })
          }

          // Visit topology members and compute unresolved top
          a <- Right(a.copy(topology = Some(a.partialTopologyMap(symbol))))
          a <- super.defTopologyAnnotatedNode(a, aNode)
          // Use the updated analysis to resolve top
          top <- Right(a.topology.get)
          top <- ResolveTopology.resolve(a, top)
        }
        yield a.copy(topologyMap = a.topologyMap + (symbol -> top))
      case _ => {
        // Topology is already in the map: nothing to do
        Right(a)
      }
    }
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
