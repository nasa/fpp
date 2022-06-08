package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for struct definitions */
object StructXmlWriter extends AstVisitor with LineUtils {

  type In = XmlWriterState

  type Out = List[Line]

  override def default(s: XmlWriterState) = Nil

  override def defStructAnnotatedNode(s: XmlWriterState, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val node = aNode._2
    val data = node.data
    val tags = {
      val pairs = s.getNamespaceAndName(Symbol.Struct(aNode))
      XmlTags.tags("serializable", pairs)
    }
    val body = {
      val Right(a) = UsedSymbols.defStructAnnotatedNode(s.a, aNode)
      val comment = AnnotationXmlWriter.multilineComment(aNode)
      val imports = s.writeImportDirectives(a.usedSymbolSet)
      val members = {
        val tags = XmlTags.tags("members")
        val st @ Type.Struct(_, _, _, _, _) = s.a.typeMap(node.id) 
        val ls = data.members.flatMap(structTypeMemberAnnotatedNode(s, st, _))
        XmlTags.taggedLines (tags) (ls.map(indentIn))
      }
      comment ++ imports ++ members
    }
    XmlTags.taggedLines(tags)(body.map(indentIn))
  }

  def structTypeMemberAnnotatedNode(
    s: XmlWriterState,
    structType: Type.Struct,
    aNode: Ast.Annotated[AstNode[Ast.StructTypeMember]]
  ): Out = {
    val node = aNode._2
    val data = node.data
    val t = s.a.typeMap(data.typeName.id)
    val pairs = {
      val nameAndType = ("name", data.name) :: TypeXmlWriter.getPairs(s, t)
      val size = (t, structType.sizes.get(data.name)) match {
        case (_, Some(n)) => List(("array_size", n.toString))
        case _ => Nil
      }
      val format = {
        val format = structType.formats.get(data.name) match {
          case Some(format) => format
          case None => Format("", List((Format.Field.Default,"")))
        }
        val s = FormatXmlWriter.formatToString(format, List(data.typeName))
        List(("format", s))
      }
      val comment = AnnotationXmlWriter.singleLineComment(aNode) match {
        case Some(comment) => List(comment)
        case None => Nil
      }
      nameAndType ++ size ++ format ++ comment
    }
    val body = {
      val defaultValue = structType.getDefaultValue.get.
        anonStruct.members(data.name)
      val xmlDefaultValue = ValueXmlWriter.write(s, defaultValue)
      val tags = XmlTags.tags("default")
      lines(XmlTags.taggedString (tags) (xmlDefaultValue))
    }
    XmlTags.taggedLines("member", pairs) (body.map(indentIn))
  }

}
