package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for port definitions */
object PortXmlWriter extends AstVisitor with LineUtils {

  override def default(s: XmlWriterState) = Nil

  override def defPortAnnotatedNode(
    s: XmlWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefPort]]
  ) = {
    val node = aNode._2
    val loc = Locations.get(node.id)
    val data = node.data
    val tags = {
      val pairs = s.getNamespaceAndName(data.name)
      XmlTags.tags("interface", pairs)
    }
    val body = {
      val comment = AnnotationXmlWriter.multilineComment(aNode)
      val imports = {
        val Right(a1) = UsedSymbols.defPortAnnotatedNode(s.a, aNode)
        val s1 = s.copy(a = a1)
        s1.writeImportDirectives
      }
      val args = formalParamList(s, data.params)
      val ret = data.returnType match {
        case Some(typeName) =>
          val t = s.a.typeMap(typeName.id)
          val pairs = TypeXmlWriter.getPairs(s, t)
          lines(XmlTags.openCloseTag("return", pairs))
        case None => Nil
      }
      comment ++ imports ++ args ++ ret
    }
    XmlTags.taggedLines(tags)(body.map(indentIn))
  }

  def formalParamList(
    s: XmlWriterState,
    params: Ast.FormalParamList
  ) = params match {
    case Nil => Nil
    case _ =>
      val ls = params.flatMap(formalParamAnnotatedNode(s, _))
      val tags = XmlTags.tags("args", Nil)
      XmlTags.taggedLines (tags) (ls.map(indentIn))
  }

  def formalParamAnnotatedNode(
    s: XmlWriterState,
    aNode: Ast.Annotated[AstNode[Ast.FormalParam]]
  ) = {
    val node = aNode._2
    val data = node.data
    val pairs = {
      val t = s.a.typeMap(data.typeName.id)
      val passBy = data.kind match {
        case Ast.FormalParam.Ref => List(("pass_by", "reference"))
        case _ => Nil
      }
      ("name", data.name) :: (TypeXmlWriter.getPairs(s, t) ++ passBy)
    }
    AnnotationXmlWriter.multilineComment(aNode) match {
      case Nil => lines(XmlTags.openCloseTag("arg", pairs))
      case comment =>
        val tags = XmlTags.tags("arg", pairs)
        XmlTags.taggedLines (tags) (comment.map(indentIn))
    }
  }

  type In = XmlWriterState

  type Out = List[Line]

}
