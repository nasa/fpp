package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write out F Prime XML for port definitions */
object PortXmlWriter extends AstVisitor with LineUtils {

  type In = XmlWriterState

  type Out = List[Line]

  override def default(s: XmlWriterState) = Nil

  override def defPortAnnotatedNode(
    s: XmlWriterState,
    aNode: Ast.Annotated[AstNode[Ast.DefPort]]
  ) = {
    val node = aNode._2
    val data = node.data
    val tags = {
      val pairs = s.getNamespaceAndName(Symbol.Port(aNode))
      XmlTags.tags("interface", pairs)
    }
    val body = {
      val comment = AnnotationXmlWriter.multilineComment(aNode)
      val imports = {
        val Right(a) = UsedSymbols.defPortAnnotatedNode(s.a, aNode)
        s.writeImportDirectives(a.usedSymbolSet)
      }
      val args = FormalParamsXmlWriter.formalParamList(s, data.params)
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

}
