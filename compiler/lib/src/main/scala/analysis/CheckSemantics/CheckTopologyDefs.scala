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
    val a1 = a.copy(topology = Some(Topology(aNode)))
    for {
      a <- super.defTopologyAnnotatedNode(a1, aNode)
      t <- a.topology.get.complete
    }
    yield {
      val symbol = Symbol.Topology(aNode)
      a.copy(topologyMap = a.topologyMap + (symbol -> t))
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
      instance <- a.useDefMap(instanceNode.id) match {
        case cis: Symbol.ComponentInstance =>
          Right(a.componentInstanceMap(cis))
        case s => Left(
          SemanticError.InvalidSymbol(
            s.getUnqualifiedName,
            Locations.get(instanceNode.id),
            "not a component instance symbol",
            s.getLoc
          )
        )
      }
      topology <- a.topology.get.addUniqueInstance(
        instance,
        visibility,
        Locations.get(node.id)
      )
    }
    yield a.copy(topology = Some(topology))
  }

}
