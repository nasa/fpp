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
    val data = aNode._2.data
    // TODO
    Right(a)
  }

}
