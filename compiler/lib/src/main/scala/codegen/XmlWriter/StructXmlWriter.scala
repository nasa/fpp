package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for structs */
object StructXmlWriter extends AstVisitor with LineUtils {

  override def default(s: XmlWriter.State) = Nil

  override def defStructAnnotatedNode(s: XmlWriter.State, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val node = aNode._2
    val loc = Locations.get(node.getId)
    val data = node.getData
    val tags = {
      val namespace = s.getNamespace
      val namePair = ("name", data.name)
      val namespacePair = ("namespace", namespace)
      val pairs = if (namespace != "") List(namespacePair, namePair) else List(namePair)
      XmlTags.tags("serializable", pairs)
    }
    val body: List[Line] = {
      val Right(a1) = UsedSymbols.defStructAnnotatedNode(s.a, aNode)
      val s1 = s.copy(a = a1)
      val imports = s1.writeImportDirectives(loc.file)
      val comment = AnnotationXmlWriter.multilineComment(aNode)
      val members = {
        val tags = XmlTags.tags("members")
        val ls = data.members.map(structTypeMemberAnnotatedNode(s, _))
        XmlTags.taggedLines(tags)(ls.map(indentIn))
      }
      comment ++ imports ++ members
    }
    XmlTags.taggedLines(tags)(body.map(indentIn))
  }

  def structTypeMemberAnnotatedNode(
    s: XmlWriter.State,
    aNode: Ast.Annotated[AstNode[Ast.StructTypeMember]]
  ) = {
    val node = aNode._2
    val data = node.getData
    val pairs = ("name", data.name) :: TypeXmlWriter.getPairs(s, data.typeName)
    val pairs1 = AnnotationXmlWriter.singleLineComment(aNode) match {
      case Some(comment) => pairs :+ comment
      case None => pairs
    }
    // TODO: Format
    line(XmlTags.openCloseTag("member", pairs1))
  }

  type In = XmlWriter.State

  type Out = List[Line]

}
