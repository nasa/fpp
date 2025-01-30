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
    val d = {
      val d1 = Dictionary()
      val d2 = DictionaryUsedSymbols(a, t).updateUsedSymbols(d1)
      DictionaryEntries(a, t).updateEntries(d2)
    }
    val a1 = a.copy(dictionary = Some(d))
    for {
      a <- super.defTopologyAnnotatedNode(a1, aNode)
    }
    yield {
      val d = a.dictionary.get
      a.copy(dictionaryMap = a.dictionaryMap + (symbol -> d))
    }
  }

}
