package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check topology instances */
object CheckTopologyInstances
  extends Analyzer
  with ModuleAnalyzer
  with TopologyAnalyzer
{

  override def defTopologyAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val symbol = Symbol.Topology(aNode)
    a.partialTopologyMap.get(symbol) match {
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
        }
        yield a.copy(
          partialTopologyMap = a.partialTopologyMap + (symbol -> top)
        )
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

}
