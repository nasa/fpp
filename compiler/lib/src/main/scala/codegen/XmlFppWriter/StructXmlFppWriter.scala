package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._
import scala.xml.Node

/** Writes out an F Prime XML Serializable struct as FPP source */
object StructXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMemberList(file))
      yield FppWriter.tuMemberList(tuMember)

  /** Builds FPP for translating Serializable XML */
  private object FppBuilder {

    /** Translates an XML type to an FPP type name */
    def translateType(file: XmlFppWriter.File): Node => Result.Result[Ast.TypeName] = 
      file.translateType(node => file.getAttribute(node, "type")) _

    /** Extracts a struct type member */
    def structTypeMemberAnnotatedNode(
      file: XmlFppWriter.File,
      node: scala.xml.Node
    ): Result.Result[Ast.Annotated[AstNode[Ast.StructTypeMember]]] = {
      for {
        structName <- file.getAttribute(file.elem, "name")
        memberName <- file.getAttribute(node, "name")
        xmlType <- file.getAttribute(node, "type")
        memberType <- translateType(file)(node)
      }
      yield {
        val xmlSizeOpt = XmlFppWriter.getAttributeOpt(node, "array_size")
        val sizeOpt = (memberType, xmlSizeOpt) match {
          case (_, Some(size)) => Some(size)
          case _ => None
        }
        val xmlFormatOpt = XmlFppWriter.getAttributeOpt(node, "format")
        val (fppFormatOpt, pre) =
          XmlFppWriter.FppBuilder.translateFormatOpt(xmlFormatOpt)
        val data = Ast.StructTypeMember(
          memberName,
          sizeOpt.map(size => AstNode.create(Ast.ExprLiteralInt(size))),
          AstNode.create(memberType),
          fppFormatOpt.map(AstNode.create(_))
        )
        val astNode = AstNode.create(data)
        val post = XmlFppWriter.getAttributeComment(node)
        (pre, astNode, post)
      }
    }

    /** Extracts enum definitions from struct members */
    def defEnumAnnotatedList(file: XmlFppWriter.File):
      Result.Result[List[Ast.Annotated[Ast.DefEnum]]] =
      for {
        child <- file.getSingleChild(file.elem, "members")
        members <- Right((child \ "member").toList)
        enumOpts <- Result.map(
          members,
          XmlFppWriter.FppBuilder.InlineEnumBuilder.defEnumAnnotatedOpt(file)
        )
      }
      yield enumOpts.filter(_.isDefined).map(_.get)

    /** Extracts struct type members */
    def structTypeMemberAnnotatedNodeList(file: XmlFppWriter.File): 
      Result.Result[List[Ast.Annotated[AstNode[Ast.StructTypeMember]]]] =
      for {
        child <- file.getSingleChild(file.elem, "members")
        result <- {
          val members = child \ "member"
          Result.map(members.toList, structTypeMemberAnnotatedNode(file, _))
        } 
      } yield result

    /** Generates the list of TU members */
    def tuMemberList(file: XmlFppWriter.File): Result.Result[List[Ast.TUMember]] =
      for {
        enums <- defEnumAnnotatedList(file)
        struct <- defStructAnnotated(file)
      }
      yield XmlFppWriter.tuMemberList(
        enums,
        Ast.TUMember.DefEnum.apply,
        Ast.ModuleMember.DefEnum.apply,
        struct,
        Ast.TUMember.DefStruct.apply,
        Ast.ModuleMember.DefStruct.apply,
        file,
      )

    /** Translates the struct type */
    def defStructAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.DefStruct]] =
      for {
        comment <- file.getComment(file.elem)
        structName <- file.getAttribute(file.elem, "name")
        members <- structTypeMemberAnnotatedNodeList(file)
      }
      yield (comment, Ast.DefStruct(structName, members, None), Nil)

  }

}
