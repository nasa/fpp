package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out an F Prime XML Serializable struct as FPP source */
object StructXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMemberList(file))
      yield FppWriter.tuMemberList(tuMember)

  /** Builds FPP for translating Serializable XML */
  private object FppBuilder {

    /** Translates an XML type to an FPP type name */
    def translateType(file: XmlFppWriter.File) = 
      file.translateType(node => file.getAttribute(node, "type")) _

    /** Constructs an array name from a struct name and a member name */
    def getArrayName(structName: String, memberName: String) =
      s"${structName}_${memberName}"

    /** Extracts a struct type member */
    def structTypeMemberAnnotatedNode(file: XmlFppWriter.File, node: scala.xml.Node): 
      Result.Result[Ast.Annotated[AstNode[Ast.StructTypeMember]]] = {
      val sizeOpt = XmlFppWriter.getAttributeOpt(node, "size")
      for {
        structName <- file.getAttribute(file.elem, "name")
        memberName <- file.getAttribute(node, "name")
        xmlType <- file.getAttribute(node, "type")
        memberType <- {
          (xmlType, sizeOpt) match {
            case ("string", _) => translateType(file)(node)
            case (_, Some(size)) => {
              val arrayName = getArrayName(structName, memberName)
              val arrayType = XmlFppWriter.FppBuilder.translateQualIdentType(arrayName)
              Right(arrayType)
            }
            case _ => translateType(file)(node)
          }
        }
      }
      yield {
        val xmlFormatOpt = sizeOpt match {
          case Some(_) => None
          case None => XmlFppWriter.getAttributeOpt(node, "format")
        }
        val (fppFormatOpt, pre) = XmlFppWriter.FppBuilder.translateFormatOpt(xmlFormatOpt)
        val data = Ast.StructTypeMember(
          memberName,
          AstNode.create(memberType),
          fppFormatOpt.map(AstNode.create(_))
        )
        val astNode = AstNode.create(data)
        val post = XmlFppWriter.getAttributeComment(node)
        (pre, astNode, post)
      }
    }

    /** Extracts an array definition from a struct member if needed */
    def defArrayAnnotatedOpt(file: XmlFppWriter.File)(structName:String)(node: scala.xml.Node):
      Result.Result[Option[Ast.Annotated[Ast.DefArray]]] =
      for {
        memberName <- file.getAttribute(node, "name")
        xmlType <- file.getAttribute(node, "type")
        result <- {
          val sizeOpt = XmlFppWriter.getAttributeOpt(node, "size")
          val xmlFormatOpt = XmlFppWriter.getAttributeOpt(node, "format")
          val (fppFormatOpt, pre) = XmlFppWriter.FppBuilder.translateFormatOpt(xmlFormatOpt)
          (xmlType, sizeOpt) match {
            case ("string", _) => Right(None)
            case (_, None) => Right(None)
            case (_, Some(size)) => for (memberType <- translateType(file)(node))
              yield {
                val array = Ast.DefArray(
                  getArrayName(structName, memberName),
                  AstNode.create(Ast.ExprLiteralInt(size)),
                  AstNode.create(memberType),
                  None,
                  fppFormatOpt.map(AstNode.create(_))
                )
                Some((pre, array, Nil))
              }
          }
        }
      }
      yield result

    /** Extracts array definitions from struct members */
    def defArrayAnnotatedList(file: XmlFppWriter.File):
      Result.Result[List[Ast.Annotated[Ast.DefArray]]] =
      for {
        name <- file.getAttribute(file.elem, "name")
        child <- file.getSingleChild(file.elem, "members")
        members <- Right((child \ "member").toList)
        arrayOpts <- Result.map(members, defArrayAnnotatedOpt(file)(name))
      }
      yield arrayOpts.filter(_.isDefined).map(_.get)

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
        arrays <- defArrayAnnotatedList(file)
        enums <- defEnumAnnotatedList(file)
        struct <- defStructAnnotated(file)
      }
      yield XmlFppWriter.tuMemberList(
        arrays,
        enums,
        struct,
        Ast.TUMember.DefStruct(_),
        Ast.ModuleMember.DefStruct(_),
        file
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
