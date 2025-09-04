package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._
import scala.xml.Node

/** Writes out an F Prime XML port as FPP source */
object PortXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMemberList(file))
      yield FppWriter.tuMemberList(tuMember)

  /** Builds FPP for translating Serializable XML */
  private object FppBuilder {

    /** Translates an XML type to an FPP type name */
    def translateType(file: XmlFppWriter.File): Node => Result.Result[Ast.TypeName] = 
      file.translateType(node => file.getAttribute(node, "type")) _

    /** Extracts the return type */
    def returnTypeOpt(file: XmlFppWriter.File):
      Result.Result[Option[AstNode[Ast.TypeName]]] =
      file.getSingleChildOpt(file.elem, "return") match {
        case Right(Some(child)) =>
          for (typeName <- translateType(file)(child))
            yield Some(AstNode.create(typeName))
        case Right(None) => Right(None)
        case Left(e) => Left(e)
      }

    /** Generates the list of TU members */
    def tuMemberList(file: XmlFppWriter.File): Result.Result[List[Ast.TUMember]] =
      for {
        enums <- FormalParamsXmlFppWriter.defEnumAnnotatedList(file, file.elem)
        port <- defPortAnnotated(file)
      }
      yield XmlFppWriter.tuMemberList(
        enums,
        Ast.TUMember.DefEnum.apply,
        Ast.ModuleMember.DefEnum.apply,
        port,
        Ast.TUMember.DefPort.apply,
        Ast.ModuleMember.DefPort.apply,
        file
      )

    /** Translates the port */
    def defPortAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.DefPort]] =
      for {
        comment <- file.getComment(file.elem)
        name <- file.getAttribute(file.elem, "name")
        params <- FormalParamsXmlFppWriter.formalParamList(file, file.elem)
        returnType <- returnTypeOpt(file)
      }
      yield (comment, Ast.DefPort(name, params, returnType), Nil)

  }

}
