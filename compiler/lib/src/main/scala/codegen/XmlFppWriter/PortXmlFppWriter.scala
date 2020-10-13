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

    /** Constructs an array name from a struct name and a member name */
    def getArrayName(structName: String, memberName: String) =
      s"${structName}_${memberName}"

    /** Extracts a struct type member */
    /*
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
    */

    /** Extracts enum definitions from struct members */
    def defEnumAnnotatedList(file: XmlFppWriter.File):
      Result.Result[List[Ast.Annotated[Ast.DefEnum]]] =
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

    /** Extracts struct type members */
    /*
    def structTypeMemberAnnotatedNodeList(file: XmlFppWriter.File): 
      Result.Result[List[Ast.Annotated[AstNode[Ast.StructTypeMember]]]] =
      for {
        child <- file.getSingleChild(file.elem, "members")
        result <- {
          val members = child \ "member"
          Result.map(members.toList, structTypeMemberAnnotatedNode(file, _))
        } 
      } yield result
      */
    
    /** Extracts the formal parameter list */
    def formalParamList(file: XmlFppWriter.File):
      Result.Result[Ast.FormalParamList] =
      Right(Nil)

    /** Extracts the return type */
    def getReturnType(file: XmlFppWriter.File):
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
        enums <- defEnumAnnotatedList(file)
        port <- defPortAnnotated(file)
      }
      yield XmlFppWriter.tuMemberList(
        Nil,
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
        comment <- file.getComment
        name <- file.getAttribute(file.elem, "name")
        params <- formalParamList(file)
        returnType <- getReturnType(file)
      }
      yield (comment, Ast.DefPort(name, params, returnType), Nil)

  }

}
