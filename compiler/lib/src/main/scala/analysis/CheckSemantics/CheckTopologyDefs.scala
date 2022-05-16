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
        val a1 = a.copy(topology = Some(Topology(aNode)))
        for {
          // Visit topology members and compute unresolved top
          a <- super.defTopologyAnnotatedNode(a1, aNode)
          top <- Right(a.topology.get)
          a <- {
            // Resolve topologies directly imported by top, updating a
            val tops = top.directImportMap.toList
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

  override def specCompInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecCompInstance]]
  ) = {
    val node = aNode._2
    val visibility = node.data.visibility
    val instanceNode = node.data.instance
    for {
      instance <- a.getComponentInstance(instanceNode.id)
      topology <- a.topology.get.addUniqueInstance(
        instance,
        visibility,
        Locations.get(node.id)
      )
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

  override def specTopImportAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTopImport]]
  ) = {
    val node = aNode._2
    val topNode = node.data.top
    for {
      ts <- a.getTopologySymbol(topNode.id)
      topology <- a.topology.get.addImportedTopology(
        ts,
        Locations.get(node.id)
      )
    }
    yield a.copy(topology = Some(topology))
  }

}
