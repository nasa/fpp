package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.analysis
import fpp.compiler.codegen._
import fpp.compiler.util._
import scala.xml.Node

/** Writes out an F Prime XML array as FPP source */
object ArrayXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMember(file))
      yield FppWriter.tuMember(tuMember)

  private object FppBuilder {

    /** Translates an XML type to an FPP type name */
    def translateType(file: XmlFppWriter.File): Node => Result.Result[Ast.TypeName] = file.translateType(node => Right(node.text)) _

    /** Translates a block of default values from FPP to XML */
    def translateDefaults(node: scala.xml.Node, tn: Ast.TypeName): (Option[AstNode[Ast.Expr]], List[String]) = {
      val xmlElements = node \ "value"
      val arrayNodeOpt = for {
        elementNodes <- Options.map(
          xmlElements.toList,
          ((node: scala.xml.Node) => XmlFppWriter.FppBuilder.translateValue(node.text, tn))
        )
      } yield AstNode.create(Ast.ExprArray(elementNodes))
      val note = arrayNodeOpt match {
        case None => 
          val xmlArray = "[ " ++ xmlElements.map(_.text).mkString(", ") ++ " ]"
          val s = "could not translate array value " ++ xmlArray
          List(XmlFppWriter.constructNote(s))
        case _ => Nil
      }
      (arrayNodeOpt, note)
    }

   /** Extracts array definitions from struct members */
    def defArrayAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.DefArray]] =
      for {
        name <- file.getAttribute(file.elem, "name")
        comment <- file.getComment(file.elem)
        xmlSize <- file.getSingleChild(file.elem, "size")
        xmlEltType <- file.getSingleChild(file.elem, "type")
        eltType <- translateType(file)(xmlEltType)
        xmlDefault <- file.getSingleChild(file.elem, "default")
        xmlFormat <- file.getSingleChild(file.elem, "format")
      }
      yield {
        val (fppDefaultsOpt, note1) = translateDefaults(xmlDefault, eltType)
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
        (note ++ comment, node, Nil)
      }

    /** Generates the TU member */
    def tuMember(file: XmlFppWriter.File): Result.Result[Ast.TUMember] =
      for (array <- defArrayAnnotated(file))
        yield XmlFppWriter.tuMember(
          array,
          Ast.TUMember.DefArray.apply,
          Ast.ModuleMember.DefArray.apply,
          file
        )

  }

}
