package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.analysis
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out an F Prime XML array as FPP source */
object ArrayXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMember(file))
      yield FppWriter.tuMember(tuMember)

  private object FppBuilder {

    /** Translates an XML type to an FPP type name */
    def translateType(file: XmlFppWriter.File) = file.translateType(node => Right(node.text)) _

   /** Extracts array definitions from struct members */
    def defArrayAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.DefArray]] =
      for {
        name <- file.getAttribute(file.elem, "name")
        comment <- file.getComment
        xmlSize <- file.getSingleChild(file.elem, "size")
        xmlEltType <- file.getSingleChild(file.elem, "type")
        eltType <- translateType(file)(xmlEltType)
        xmlDefault <- file.getSingleChild(file.elem, "default")
        xmlFormat <- file.getSingleChild(file.elem, "format")
      }
      yield {
        val (fppDefaultsOpt, note1) = 
          XmlFppWriter.FppBuilder.translateDefaults(xmlDefault, eltType)
        val (fppFormatOpt, note2) = 
          XmlFppWriter.FppBuilder.translateFormatOpt(Some(xmlFormat.text))
        val note = note1 ++ note2
        val node = Ast.DefArray(
          name,
          AstNode.create(Ast.ExprLiteralInt(xmlSize.text)),
          AstNode.create(eltType),
          fppDefaultsOpt,
          fppFormatOpt.map(AstNode.create(_))
        )
        (note, node, comment)
      }

    /** Generates the list of TU members */
    def tuMember(file: XmlFppWriter.File): Result.Result[Ast.TUMember] =
      for (data <- defArrayAnnotated(file))
      yield {
        def transform[A,B](construct: AstNode[A] => B)(a: Ast.Annotated[A]) = 
          (a._1, construct(AstNode.create(a._2)), a._3)
        val moduleNames = XmlFppWriter.getAttributeNamespace(file.elem)
        val aNode = moduleNames match {
          case Nil => transform(Ast.TUMember.DefArray)(data)
          case head :: tail => {
            val aNodeList1 = List(transform(Ast.TUMember.DefArray)(data))
            val aNodeList2 = XmlFppWriter.FppBuilder.encloseWithModuleMemberModules(tail.reverse)(aNodeList1)
            XmlFppWriter.FppBuilder.encloseWithTuMemberModule(head)(aNodeList2)
          }
        }
        Ast.TUMember(aNode)
      }

  }

}
