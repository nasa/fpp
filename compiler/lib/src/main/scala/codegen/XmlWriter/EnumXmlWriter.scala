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
    val body = {
      // The Enum XML schema currently does not allow a top-level comment
      val comment = Nil
      val members = data.constants.map(defEnumConstantAnnotatedNode(s, _))
      comment ++ members
    }
    XmlTags.taggedLines(tags)(body.map(indentIn))
  }

  def defEnumConstantAnnotatedNode(
    s: XmlWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefEnumConstant]]
  ): Line = {
    val node = aNode._2
    val data = node.getData
    val namePair = ("name", data.name)
    val valuePair = {
      val Value.EnumConstant(value, _) = s.a.valueMap(node.getId)
      ("value", value._2.toString)
    }
    val pairs = AnnotationXmlWriter.singleLineComment(aNode) match {
      case Some(commentPair) => List(namePair, valuePair, commentPair)
      case None => List(namePair, valuePair)
    }
    line(XmlTags.openCloseTag("item", pairs))
  }

  type In = XmlWriterState

  type Out = List[Line]

}
