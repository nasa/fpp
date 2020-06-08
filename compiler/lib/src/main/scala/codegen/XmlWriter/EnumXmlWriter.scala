package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for enum definitions */
object EnumXmlWriter extends AstVisitor with LineUtils {

  override def default(s: XmlWriterState) = Nil

  override def defEnumAnnotatedNode(s: XmlWriterState, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val node = aNode._2
    val loc = Locations.get(node.getId)
    val data = node.getData
    val tags = {
      val pairs = s.getNamespaceAndName(data.name)
      XmlTags.tags("enum", pairs)
    }
    val body = Nil
    XmlTags.taggedLines(tags)(body.map(indentIn))
  }

  type In = XmlWriterState

  type Out = List[Line]

}
