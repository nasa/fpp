package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out an F Prime XML Port struct as FPP source */
object PortXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMemberList(file))
      yield FppWriter.tuMemberList(tuMember)

  /** Builds FPP for translating Serializable XML */
  private object FppBuilder {

    /** Translates an XML type to an FPP type name */
    def translateType(file: XmlFppWriter.File) = 
      file.translateType(node => file.getAttribute(node, "type")) _

    /** Extracts a formal parameter */
    def formalParamAnnotatedNode(file: XmlFppWriter.File, node: scala.xml.Node): 
      Result.Result[Ast.Annotated[AstNode[Ast.FormalParam]]] =
      for {
        name <- file.getAttribute(node, "name")
        kind <- XmlFppWriter.getAttributeOpt(node, "pass_by") match {
          case Some("value") => Right(Ast.FormalParam.Value)
          case Some("reference") => Right(Ast.FormalParam.Ref)
          case Some(s) => 
            Left(
              file.error(XmlError.SemanticError(_, s"invalid attribute pass_by in node ${node.toString}"))
            )
          case None => Right(Ast.FormalParam.Value)
        }
        typeName <- translateType(file)(node)
        comment <- Right(Nil)
      }
      yield {
        val typeNameNode = AstNode.create(typeName)
        val data = Ast.FormalParam(kind, name, typeNameNode)
        val node = AstNode.create(data)
        (Nil, node, comment)
      }

    /** Extracts enum definitions from argument and return types */
    def defEnumAnnotatedList(file: XmlFppWriter.File):
      Result.Result[List[Ast.Annotated[Ast.DefEnum]]] =
      // TODO
      /*
      for {
        child <- file.getSingleChild(file.elem, "members")
        members <- Right((child \ "member").toList)
        enumOpts <- Result.map(
          members,
          XmlFppWriter.FppBuilder.InlineEnumBuilder.defEnumAnnotatedOpt(file)
        )
      }
      yield enumOpts.filter(_.isDefined).map(_.get)
      */
      Right(Nil)

    /** Extracts formal parameters */
    def formalParamList(file: XmlFppWriter.File): Result.Result[Ast.FormalParamList] =
      for {
        childOpt <- file.getSingleChildOpt(file.elem, "args")
        result <- childOpt match {
          case Some(child) =>
            val args = child \ "arg"
            Result.map(args.toList, formalParamAnnotatedNode(file, _))
          case None => Right(Nil)
        }
      } yield result

    /** Extracts the return type */
    def typeNameNodeOpt(file: XmlFppWriter.File):
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
        arrays <- Right(Nil)
        enums <- defEnumAnnotatedList(file)
        port <- defPortAnnotated(file)
      }
      yield XmlFppWriter.tuMemberList(
        arrays,
        enums,
        port,
        Ast.TUMember.DefPort(_),
        Ast.ModuleMember.DefPort(_),
        file
      )

    /** Translates the port */
    def defPortAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.DefPort]] =
      for {
        comment <- file.getComment(file.elem)
        name <- file.getAttribute(file.elem, "name")
        params <- formalParamList(file)
        returnType <- typeNameNodeOpt(file)
      }
      yield (comment, Ast.DefPort(name, params, returnType), Nil)

  }

}
