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

    /** Constructs an array name from a struct name and a member name */
    def getArrayName(structName: String, memberName: String) =
      s"${structName}_${memberName}"

    /** Extracts a struct type member */
    def structTypeMemberAnnotatedNode(file: XmlFppWriter.File, node: scala.xml.Node): 
      Result.Result[Ast.Annotated[AstNode[Ast.StructTypeMember]]] =
      for {
        structName <- file.getAttribute(file.elem, "name")
        memberName <- file.getAttribute(node, "name")
        xmlType <- file.getAttribute(node, "type")
        memberType <- {
          val sizeOpt = XmlFppWriter.getAttributeOpt(node, "size")
          (xmlType, sizeOpt) match {
            case ("string", _) => file.translateType(node)
            case (_, Some(size)) => {
              val arrayName = getArrayName(structName, memberName)
              val arrayType = XmlFppWriter.FppBuilder.translateQualIdentType(arrayName)
              Right(arrayType)
            }
            case _ => file.translateType(node)
          }
        }
      }
      yield {
        val comment = XmlFppWriter.getAttributeComment(node)
        val data = Ast.StructTypeMember(memberName, AstNode.create(memberType), None)
        val astNode = AstNode.create(data)
        (Nil, astNode, comment)
      }

    /** Extracts an array definition from a struct member if needed */
    def defArrayAnnotatedOpt(file: XmlFppWriter.File)(structName:String)(node: scala.xml.Node):
      Result.Result[Option[Ast.Annotated[Ast.DefArray]]] =
      for {
        memberName <- file.getAttribute(node, "name")
        xmlType <- file.getAttribute(node, "type")
        result <- {
          val sizeOpt = XmlFppWriter.getAttributeOpt(node, "size")
          (xmlType, sizeOpt) match {
            case ("string", _) => Right(None)
            case (_, None) => Right(None)
            case (_, Some(size)) => 
              for (memberType <- file.translateType(node))
                yield {
                  val array = Ast.DefArray(
                    getArrayName(structName, memberName),
                    AstNode.create(Ast.ExprLiteralInt(size)),
                    AstNode.create(memberType),
                    None,
                    None
                  )
                  Some((Nil, array, Nil))
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
    def tuMemberList(file: XmlFppWriter.File): Result.Result[List[Ast.TUMember]] = {
      for {
        arrays <- defArrayAnnotatedList(file)
        enums <- defEnumAnnotatedList(file)
        struct <- defStructAnnotated(file)
      }
      yield {
        def transform[A,B](construct: AstNode[A] => B)(a: Ast.Annotated[A]) = 
          (a._1, construct(AstNode.create(a._2)), a._3)
        val moduleNames = XmlFppWriter.getAttributeNamespace(file.elem)
        val memberNodes = moduleNames match {
          case Nil => {
            val arrays1 = arrays.map(transform(Ast.TUMember.DefArray))
            val enums1 = enums.map(transform(Ast.TUMember.DefEnum))
            val struct1 = transform(Ast.TUMember.DefStruct)(struct)
            (arrays1 ++ enums1) :+ struct1
          }
          case head :: tail => {
            val arrays1 = arrays.map(transform(Ast.ModuleMember.DefArray))
            val enums1 = enums.map(transform(Ast.ModuleMember.DefEnum))
            val struct1 = transform(Ast.ModuleMember.DefStruct)(struct)
            val aNodeList1 = (arrays1 ++ enums1) :+ struct1
            val aNodeList2 = XmlFppWriter.FppBuilder.encloseWithModuleMemberModules(tail.reverse)(aNodeList1)
            List(XmlFppWriter.FppBuilder.encloseWithTuMemberModule(head)(aNodeList2))
          }
        }
        memberNodes.map(Ast.TUMember(_))
      }      
    }

    /** Translates the struct type */
    def defStructAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.DefStruct]] =
      for {
        comment <- file.getComment
        structName <- file.getAttribute(file.elem, "name")
        members <- structTypeMemberAnnotatedNodeList(file)
      }
      yield (comment, Ast.DefStruct(structName, members, None), Nil)

  }

}
