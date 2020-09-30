package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for port definitions */
object PortXmlWriter extends AstVisitor with LineUtils {

  override def default(s: XmlWriterState) = Nil

  override def defPortAnnotatedNode(s: XmlWriterState, aNode: Ast.Annotated[AstNode[Ast.DefPort]]) = {
    val node = aNode._2
    val loc = Locations.get(node.getId)
    val data = node.getData
    /*
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
        val st @ Type.Struct(_, _, _, _) = s.a.typeMap(node.getId) 
        val ls = data.members.map(structTypeMemberAnnotatedNode(s, st, _))
        XmlTags.taggedLines(tags)(ls.map(indentIn))
      }
      comment ++ imports ++ members
    }
    XmlTags.taggedLines(tags)(body.map(indentIn))
    */
    Nil
  }

  type In = XmlWriterState

  type Out = List[Line]

}
