package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Construct the dictionary map */
object ConstructDictionaryMap
  extends Analyzer 
  with ModuleAnalyzer
  with TopologyAnalyzer
  with TlmPacketSetAnalyzer
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
    val a1 = a.copy(topology = Some(t), dictionary = Some(d))
    for {
      a <- super.defTopologyAnnotatedNode(a1, aNode)
    }
    yield {
      val d = a.dictionary.get
      a.copy(dictionaryMap = a.dictionaryMap + (symbol -> d))
    }
  }

  override def specTlmPacketSetAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacketSet]]
  ) = {
    val tpgOpt = Some(TlmPacketSet(aNode))
    val a1 = a.copy(tlmPacketSet = tpgOpt)
    val d = a.dictionary.get
    for {
      a <- super.specTlmPacketSetAnnotatedNode(a1, aNode)
      tpg <- TlmPacketSet.complete (
        a,
        d,
        a.topology.get
      ) (a.tlmPacketSet.get)
      d <- d.addTlmPacketSet(tpg)
    }
    yield a.copy(dictionary = Some(d))
  }

  override def specTlmPacketAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacket]]
  ) = {
    val data = aNode._2.data
    for {
      idOpt <- a.getNonnegativeBigIntValueOpt(data.id)
      packet <- TlmPacket.fromSpecTlmPacket(
        a,
        a.dictionary.get,
        a.topology.get,
        aNode
      )
      g <- a.tlmPacketSet.get.addPacket(idOpt, packet)
    }
    yield a.copy(tlmPacketSet = Some(g))
  }

}
