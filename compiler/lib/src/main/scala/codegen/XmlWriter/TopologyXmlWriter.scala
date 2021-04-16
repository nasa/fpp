package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for topology definitions */
object TopologyXmlWriter extends AstVisitor with LineUtils {

  override def defTopologyAnnotatedNode(
    s: XmlWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val symbol = Symbol.Topology(aNode)
    val t = s.a.topologyMap(symbol)
    val data = aNode._2.data
    val pairs = List(("name", s.getName(symbol)))
    val body = {
      List(
        writeImports(s, t),
        writeInstances(s, t),
        writeConnections(s, t)
      ).flatMap(XmlWriterState.addBlankPrefix) :+ Line.blank
    }
    XmlTags.taggedLines ("assembly", pairs) (body.map(indentIn))
  }

  override def default(s: XmlWriterState) = Nil

  private def writeImports(s: XmlWriterState, t: Topology) = {
    val symbols = t.instanceMap.keys.map(ci => Symbol.Component(ci.component.aNode))
    s.writeImportDirectives(symbols)
  }

  private def writeInstances(s: XmlWriterState, t: Topology) = {
    def writeInstance(ci: ComponentInstance) = {
      val cis = Symbol.ComponentInstance(ci.aNode)
      val cs = Symbol.Component(ci.component.aNode)
      val pairs = s.getNamespaceAndName(cis) ++ List(
        ("type", s.writeSymbol(cs)),
        ("base_id", XmlWriterState.writeId(ci.baseId)),
        ("base_id_window", XmlWriterState.writeId(ci.maxId - ci.baseId + 1))
      )
      XmlTags.taggedLines ("instance", pairs) (Nil)
    }
    t.instanceMap.keys.flatMap(writeInstance).toList
  }

  private def writeConnections(s: XmlWriterState, t: Topology) = {
    def writeGraph(graphName: String): List[Line] = {
      List (
        XmlWriterState.writeComment(s"@FPL START $graphName"),
        // TODO
        Nil,
        XmlWriterState.writeComment(s"@FPL END")
      ).flatten
    }
    Line.blankSeparated (writeGraph) (t.connectionMap.keys.toList.sorted)
  }

  type In = XmlWriterState

  type Out = List[Line]

}
