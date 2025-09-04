package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out an F Prime XML enum as FPP source */
object EnumXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMember(file))
      yield FppWriter.tuMember(tuMember)

  private object FppBuilder {

    def annotation(file: XmlFppWriter.File): List[String] = {
      // Not supported in F Prime XML
      Nil
    }

    def constants(file: XmlFppWriter.File): Result.Result[List[Ast.Annotated[AstNode[Ast.DefEnumConstant]]]] = {
      val items = file.elem \ "item"
      Result.map(items.toList, defEnumConstantAnnotatedNode(file, _))
    }

    def defEnum(file: XmlFppWriter.File): Result.Result[Ast.DefEnum] =
      for {
        name <- file.getAttribute(file.elem, "name")
        constants <- FppBuilder.constants(file)
      }
      yield {
        val repType = FppBuilder.repType(file)
        val default = FppBuilder.default(file)
        Ast.DefEnum(name, repType, constants, default)
      }

    def defEnumConstant(
      file: XmlFppWriter.File,
      constant: scala.xml.Node
    ): Result.Result[Ast.DefEnumConstant] =
      for {
        name <- file.getAttribute(constant, "name")
        value <- file.getAttribute(constant, "value")
      }
      yield {
        val e = Ast.ExprLiteralInt(value)
        val node = AstNode.create(e)
        Ast.DefEnumConstant(name, Some(node))
      }

    def defEnumConstantAnnotatedNode(
      file: XmlFppWriter.File,
      constant: scala.xml.Node
    ): Result.Result[Ast.Annotated[AstNode[Ast.DefEnumConstant]]] =
      for (data <- defEnumConstant(file, constant))
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

    /** Generates the TU member */
    def tuMember(file: XmlFppWriter.File): Result.Result[Ast.TUMember] =
      for (data <- defEnum(file))
        yield {
          val a = annotation(file)
          val aT = (a, data, Nil)
          XmlFppWriter.tuMember(
            aT,
            Ast.TUMember.DefEnum.apply,
            Ast.ModuleMember.DefEnum.apply,
            file
          )
        }

  }

}
