package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for topology definitions */
object TopologyXmlWriter extends AstVisitor with LineUtils {

  type In = XmlWriterState

  type Out = List[Line]

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
      val pairs = List(
        ("namespace", s.cppWriterState.getNamespace(cs).getOrElse("")),
        ("name", s.getName(cis)),
        ("type", cs.getUnqualifiedName),
        ("base_id", XmlWriterState.writeId(ci.baseId)),
        ("base_id_window", (ci.maxId - ci.baseId + 1).toString)
      )
      XmlTags.taggedLines ("instance", pairs) (Nil)
    }
    val instances = t.instanceMap.keys.toArray.sortWith {
      case (a, b) => 
        if (a.baseId != b.baseId) a.baseId < b.baseId
        else a < b
    }
    //t.instanceMap.keys.toArray.sorted.flatMap(writeInstance).toList
    instances.flatMap(writeInstance).toList
  }

  private def writeConnections(s: XmlWriterState, t: Topology) = {
    def getPairs(
      endpoint: Connection.Endpoint,
      portNumber: Int
    ): List[(String, String)] = {
      val pii = endpoint.port
      List(
        ("component", pii.componentInstance.getUnqualifiedName),
        ("port", pii.portInstance.getUnqualifiedName),
        ("type", "[unused]"),
        ("num", portNumber.toString)
      )
    }
    def writeConnection(c: Connection) = {
      val pairs = List(("name", "[unused]"))
      val body = {
        val fromPortNumber = t.fromPortNumberMap(c)
        val toPortNumber = t.toPortNumberMap(c)
        val source = XmlTags.taggedLines (
          "source", getPairs(c.from, fromPortNumber)
        ) (Nil)
        val target = XmlTags.taggedLines (
          "target", getPairs(c.to, toPortNumber)
        ) (Nil)
        source ++ target
      }
      XmlTags.taggedLines ("connection", pairs) (body.map(indentIn))
    }
    def writeGraph(graphName: String): List[Line] = {
      List (
        XmlWriterState.writeComment(s"@FPL START $graphName"),
        t.sortConnections(t.connectionMap(graphName)).flatMap(writeConnection),
        XmlWriterState.writeComment(s"@FPL END")
      ).flatten
    }
    Line.blankSeparated (writeGraph) (t.connectionMap.keys.toList.sorted)
  }

}
