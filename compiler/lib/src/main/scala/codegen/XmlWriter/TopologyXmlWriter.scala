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
      def addBlank(ls: List[Line]) = ls match {
        case Nil => Nil
        case _ => Line.blank :: ls
      }
      List(
        writeImports(s, t),
        /*
        writePorts(s, c),
        writeInternalInterfaces(s, c),
        writeCommands(s, c),
        writeEvents(s, c),
        writeParams(s, c),
        writeTlmChannels(s, c)
        */
      ).flatMap(addBlank) :+ Line.blank
      Nil
    }
    XmlTags.taggedLines ("assembly", pairs) (body.map(indentIn))
  }

  override def default(s: XmlWriterState) = Nil

  private def writeImports(s: XmlWriterState, t: Topology) = {
    /*
    val Right(a1) = UsedSymbols.defTopologyAnnotatedNode(s.a, t.aNode)
    // Import only components
    val uss = a1.usedSymbolSet.filter(s => s match {
      case _: Symbol.Component => true
      case _ => false
    })
    val a2 = a1.copy(usedSymbolSet = uss)
    s.copy(a = a1).writeImportDirectives
    */
    Nil
  }

  type In = XmlWriterState

  type Out = List[Line]

}
