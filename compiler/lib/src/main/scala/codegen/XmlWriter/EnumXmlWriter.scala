package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for enum definitions */
object EnumXmlWriter extends AstVisitor with LineUtils {

  type In = XmlWriterState

  type Out = List[Line]
  
  override def default(s: XmlWriterState) = Nil

  override def defEnumAnnotatedNode(s: XmlWriterState, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val node = aNode._2
    val data = node.data
    val enumType @ Type.Enum(_, _, _) = s.a.typeMap(node.id)
    val tags = {
      val namespaceAndName = s.getNamespaceAndName(Symbol.Enum(aNode))
      val serializeType = TypeXmlWriter.getName(s, enumType.repType)
      val defaultValue = ValueXmlWriter.write(s, enumType.getDefaultValue.get).
        replaceAll("^.*::", "")
      val pairs = namespaceAndName ++ List(
        ("serialize_type", serializeType),
        ("default", defaultValue)
      )
      XmlTags.tags("enum", pairs)
    }
    val body = {
      val comment = AnnotationXmlWriter.multilineComment(aNode)
      val members = data.constants.map(defEnumConstantAnnotatedNode(s, _))
      comment ++ members
    }
    XmlTags.taggedLines(tags)(body.map(indentIn))
  }

  private def defEnumConstantAnnotatedNode(
    s: XmlWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefEnumConstant]]
  ): Line = {
    val node = aNode._2
    val data = node.data
    val namePair = ("name", data.name)
    val valuePair = {
      val Value.EnumConstant(value, _) = s.a.valueMap(node.id)
      ("value", value._2.toString)
    }
    val pairs = AnnotationXmlWriter.singleLineComment(aNode) match {
      case Some(commentPair) => List(namePair, valuePair, commentPair)
      case None => List(namePair, valuePair)
    }
    line(XmlTags.openCloseTag("item", pairs))
  }

}
