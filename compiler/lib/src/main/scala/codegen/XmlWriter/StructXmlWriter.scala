package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for struct definitions */
object StructXmlWriter extends AstVisitor with LineUtils {

  override def default(s: XmlWriterState) = Nil

  override def defStructAnnotatedNode(s: XmlWriterState, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val node = aNode._2
    val loc = Locations.get(node.id)
    val data = node.data
    val tags = {
      val pairs = s.getNamespaceAndName(data.name)
      XmlTags.tags("serializable", pairs)
    }
    val body = {
      val Right(a1) = UsedSymbols.defStructAnnotatedNode(s.a, aNode)
      val s1 = s.copy(a = a1)
      val comment = AnnotationXmlWriter.multilineComment(aNode)
      val imports = s1.writeImportDirectives(loc.file)
      val members = {
        val tags = XmlTags.tags("members")
        val st @ Type.Struct(_, _, _, _) = s.a.typeMap(node.id) 
        val ls = data.members.map(structTypeMemberAnnotatedNode(s, st, _))
        XmlTags.taggedLines(tags)(ls.map(indentIn))
      }
      comment ++ imports ++ members
    }
    XmlTags.taggedLines(tags)(body.map(indentIn))
  }

  def structTypeMemberAnnotatedNode(
    s: XmlWriterState,
    structType: Type.Struct,
    aNode: Ast.Annotated[AstNode[Ast.StructTypeMember]]
  ) = {
    val node = aNode._2
    val data = node.data
    val t = s.a.typeMap(data.typeName.id)
    val pairs = ("name", data.name) :: TypeXmlWriter.getPairs(s, t)
    val pairs1 = {
      val format = structType.formats.get(data.name) match {
        case Some(format) => format
        case None => Format("", List((Format.Field.Default,"")))
      }
      val s = FormatXmlWriter.formatToString(format, List(data.typeName))
      pairs :+ ("format", s)
    }
    val pairs2 = AnnotationXmlWriter.singleLineComment(aNode) match {
      case Some(comment) => pairs1 :+ comment
      case None => pairs1
    }
    line(XmlTags.openCloseTag("member", pairs2))
  }

  type In = XmlWriterState

  type Out = List[Line]

}
