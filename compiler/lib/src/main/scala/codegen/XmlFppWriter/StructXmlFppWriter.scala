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

    /** Extracts arrays from struct member types */
    def defArrayAnnotatedList(file: XmlFppWriter.File): Result.Result[List[Ast.Annotated[Ast.DefArray]]] = {
      def array (name:String) (node: scala.xml.Node): Result.Result[Option[Ast.DefArray]] =
        for {
          memberName <- file.getAttribute(node, "name")
          t <- file.getAttribute(node, "type")
          result <- {
            val sizeOpt = XmlFppWriter.getAttributeOpt(node, "size")
            (t, sizeOpt) match {
              case ("string", _) => Right(None)
              case (_, None) => Right(None)
              case (_, Some(size)) => for (t <- file.translateType(node)) yield
                Some(Ast.DefArray(
                  s"${name}_${memberName}",
                  AstNode.create(Ast.ExprLiteralInt(size)),
                  AstNode.create(t),
                  None,
                  None
                ))
            }
          }
        }
        yield result
      for {
        name <- file.getAttribute(file.elem, "name")
        child <- file.getSingleChild(file.elem, "members")
        members <- Right((child \ "member").toList)
        arrays <- Result.map(members, array(name))
      }
      yield arrays.filter(_.isDefined).map(_.get).map((Nil, _, Nil))
    }

    /** Extracts enums from struct member types */
    def defEnumAnnotatedList(file: XmlFppWriter.File): Result.Result[List[Ast.Annotated[Ast.DefEnum]]] = {
      for {
        child <- file.getSingleChild(file.elem, "members")
        members <- Right((child \ "member").toList)
        enumOpts <- Result.map(members, XmlFppWriter.FppBuilder.EnumBuilder.defEnumAnnotatedOpt(file))
      }
      yield enumOpts.filter(_.isDefined).map(_.get).map((Nil, _, Nil))
    }

    /** Translates the struct type */
    def defStructAnnotated(file: XmlFppWriter.File): Result.Result[Ast.Annotated[Ast.DefStruct]] =
      for {
        comment <- file.getComment
        name <- file.getAttribute(file.elem, "name")
        members <- structTypeMemberAnnotatedNodeList(file)
      }
      yield (comment, Ast.DefStruct(name, members, None), Nil)

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

    /** Extracts a struct type member */
    def structTypeMemberAnnotatedNode(file: XmlFppWriter.File, node: scala.xml.Node): 
      Result.Result[Ast.Annotated[AstNode[Ast.StructTypeMember]]] =
      for {
        name <- file.getAttribute(node, "name")
        t <- file.translateType(node)
      }
      yield {
        val comment = XmlFppWriter.getAttributeComment(node)
        val data = Ast.StructTypeMember(name, AstNode.create(t), None)
        val astNode = AstNode.create(data)
        (Nil, astNode, comment)
      }

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

  }

}
