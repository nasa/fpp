package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._
import scala.xml.Node

/** Writes out F Prime XML arguments as FPP formal parameters */
object FormalParamsXmlFppWriter extends LineUtils {

  /** Translates an XML type to an FPP type name */
  def translateType(file: XmlFppWriter.File): Node => Result.Result[Ast.TypeName] = 
    file.translateType(node => file.getAttribute(node, "type")) _

  /** Extracts a formal parameter */
  def formalParamAnnotatedNode(file: XmlFppWriter.File, node: scala.xml.Node): 
    Result.Result[Ast.Annotated[AstNode[Ast.FormalParam]]] =
    for {
      name <- file.getAttribute(node, "name")
      kind <- XmlFppWriter.getAttributeOpt(node, "pass_by") match {
        case Some("value") => Right(Ast.FormalParam.Value)
        case Some("reference") => Right(Ast.FormalParam.Ref)
        case Some(_) => Left(file.invalidAttribute("pass_by", node))
        case None => Right(Ast.FormalParam.Value)
      }
      typeName <- translateType(file)(node)
      comment <- file.getComment(node)
    }
    yield {
      val typeNameNode = AstNode.create(typeName)
      val data = Ast.FormalParam(kind, name, typeNameNode)
      val node = AstNode.create(data)
      (Nil, node, comment)
    }

  /** Extracts enum definitions from argument and return types */
  def defEnumAnnotatedList(file: XmlFppWriter.File, node: scala.xml.Node):
    Result.Result[List[Ast.Annotated[Ast.DefEnum]]] =
    for {
      nodeOpt <- file.getSingleChildOpt(node, "args")
      nodes <- nodeOpt match {
        case Some(node) => Right((node \ "arg").toList)
        case None => Right(Nil)
      }
      retTypeNodeOpt <- file.getSingleChildOpt(node, "return")
      nodes <- retTypeNodeOpt match {
        case Some(node) => Right(nodes :+ node)
        case None => Right(nodes)
      }
      enumOpts <- Result.map(
        nodes,
        XmlFppWriter.FppBuilder.InlineEnumBuilder.defEnumAnnotatedOpt(file)
      )
    }
    yield enumOpts.filter(_.isDefined).map(_.get)

  /** Extracts formal parameters */
  def formalParamList(
    file: XmlFppWriter.File,
    node: scala.xml.Node
  ): Result.Result[Ast.FormalParamList] =
    for {
      childOpt <- file.getSingleChildOpt(node, "args")
      result <- childOpt match {
        case Some(child) =>
          val args = child \ "arg"
          Result.map(args.toList, formalParamAnnotatedNode(file, _))
        case None => Right(Nil)
      }
    } yield result

}
