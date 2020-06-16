package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out an F Prime XML Serializable struct as FPP source */
object StructXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMemberList(file))
      yield FppWriter.tuMemberList(tuMember)

  private object FppBuilder {

    def annotation(file: XmlFppWriter.File): List[String] = {
      // TODO
      Nil
    }

    /** Arrays extracted from struct member types */
    def defArrayList(file: XmlFppWriter.File): Result.Result[List[Ast.DefArray]] =
      Right(Nil)

    /** Enums extracted from struct member types */
    def defEnumList(file: XmlFppWriter.File): Result.Result[List[Ast.DefEnum]] =
      Right(Nil)

    def defStruct(file: XmlFppWriter.File): Result.Result[Ast.DefStruct] =
      Right(Ast.DefStruct("TODO", Nil, None))

      /*
      for {
        name <- file.getAttribute(file.elem, "name")
        constants <- FppBuilder.constants(file)
      }
      yield {
        val repType = FppBuilder.repType(file)
        val default = FppBuilder.default(file)
        Ast.DefStruct(name, repType, constants, default)
      }

    def defStructConstant(
      file: XmlFppWriter.File,
      constant: scala.xml.Node
    ): Result.Result[Ast.DefStructConstant] =
      for {
        name <- file.getAttribute(constant, "name")
        value <- file.getAttribute(constant, "value")
      }
      yield {
        val e = Ast.ExprLiteralInt(value)
        val node = AstNode.create(e)
        Ast.DefStructConstant(name, Some(node))
      }

    def defStructConstantAnnotatedNode(
      file: XmlFppWriter.File,
      constant: scala.xml.Node
    ): Result.Result[Ast.Annotated[AstNode[Ast.DefStructConstant]]] =
      for (data <- defStructConstant(file, constant))
      yield {
        val a = XmlFppWriter.getAttributeComment(constant)
        val node = AstNode.create(data)
        (Nil, node, a)
      }

    def default(file: XmlFppWriter.File): Option[AstNode[Ast.Expr]] = {
      // Not supported in F Prime XML
      None
    }

    def repType(file: XmlFppWriter.File): Option[AstNode[Ast.TypeName]] = {
      // Not supported in F Prime XML
      None
    }
    */

    def tuMemberList(file: XmlFppWriter.File): Result.Result[List[Ast.TUMember]] =
      Right(Nil)
      /*
      for (data <- defStruct(file))
      yield {
        val a = annotation(file)
        val moduleNames = XmlFppWriter.getAttributeNamespace(file.elem)
        val node = AstNode.create(data)
        val memberNode = moduleNames match {
          case Nil => Ast.TUMember.DefStruct(node)
          case head :: tail => {
            val memberNode1 = Ast.ModuleMember.DefStruct(node)
            val memberNode2 = XmlFppWriter.FppBuilder.encloseWithModuleMemberModules(tail.reverse)(memberNode1)
            XmlFppWriter.FppBuilder.encloseWithTuMemberModule(head)(memberNode2)
          }
        }
        val aNode = (a, memberNode, Nil)
        Ast.TUMember(aNode)
      }
      */

  }

}
