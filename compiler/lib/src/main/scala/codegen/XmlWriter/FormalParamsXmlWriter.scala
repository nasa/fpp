package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for formal parameters */
object FormalParamsXmlWriter extends LineUtils {

  def formalParamList(
    s: XmlWriterState,
    params: Ast.FormalParamList
  ): List[Line] = params match {
    case Nil => Nil
    case _ =>
      val ls = params.flatMap(formalParamAnnotatedNode(s, _))
      val tags = XmlTags.tags("args", Nil)
      XmlTags.taggedLines (tags) (ls.map(indentIn))
  }

  def formalParamAnnotatedNode(
    s: XmlWriterState,
    aNode: Ast.Annotated[AstNode[Ast.FormalParam]]
  ): List[Line] = {
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

}
