package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Write out F Prime XML for structs */
object StructXmlWriter extends AstVisitor with LineUtils {

  override def default(s: XmlWriter.State) = Nil

  override def defStructAnnotatedNode(s: XmlWriter.State, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val node = aNode._2
    val data = node.getData
    val openTag = {
      val namespace = s.getNamespace
      val pairs = List(
        if (namespace != "") Some("namespace", namespace) else None,
        Some("name", data.name)
      ).filter(_.isDefined).map(_.get)
      lines(XmlTags.openTag("serializable", pairs))
    }
    val body: List[Line] = {
      val openTag = lines(XmlTags.openTag("members"))
      val members = data.members.map(structTypeMemberAnnotatedNode(s, _))
      val closeTag = lines(XmlTags.closeTag("members"))
      openTag ++ members.map(indentIn) ++ closeTag
    }
    val closeTag = lines(XmlTags.closeTag("serializable"))
    openTag ++ body.map(indentIn) ++ closeTag
  }

  def structTypeMemberAnnotatedNode(
    s: XmlWriter.State,
    aNode: Ast.Annotated[AstNode[Ast.StructTypeMember]]
  ) = {
    val node = aNode._2
    val data = node.getData
    val pairs = ("name", data.name) :: TypeXmlWriter.getPairs(s, data.typeName)
    // TODO: Add comment
    Line(XmlTags.openCloseTag("member", pairs))
  }

  type In = XmlWriter.State

  type Out = List[Line]

}
