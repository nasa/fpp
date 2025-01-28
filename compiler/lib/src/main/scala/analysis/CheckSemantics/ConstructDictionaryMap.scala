package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Construct the dictionary map */
object ConstructDictionaryMap
  extends Analyzer 
  with ModuleAnalyzer
  with TopologyAnalyzer
  with TlmPacketGroupAnalyzer
{

  override def defTopologyAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val symbol = Symbol.Topology(aNode)
    val t = a.topologyMap(symbol)
    // TODO
    val d = Dictionary.buildDictionary(a, t)
    Right(a.copy(dictionaryMap = a.dictionaryMap + (symbol -> d)))
  }

}
