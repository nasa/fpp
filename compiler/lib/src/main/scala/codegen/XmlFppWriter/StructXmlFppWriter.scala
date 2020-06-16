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

    /** Extracts arrays from struct member types */
    def defArrayAnnotatedList(file: XmlFppWriter.File): Result.Result[List[Ast.Annotated[Ast.DefArray]]] = {
      def array (name:String) (node: scala.xml.Node): Result.Result[Option[Ast.DefArray]] =
        for {
          memberName <- file.getAttribute(node, "name")
          t <- file.getAttribute(node, "type")
        }
        yield {
          val sizeOpt = XmlFppWriter.getAttributeOpt(node, "size")
          (t, sizeOpt) match {
            case ("string", _) => None
            case (_, None) => None
            case (_, Some(size)) => Some(Ast.DefArray(
              s"${name}_${memberName}",
              AstNode.create(Ast.ExprLiteralInt(size)),
              AstNode.create(XmlFppWriter.FppBuilder.translateType(t, None)),
              None,
              None
            ))
          }
        }
      for {
        name <- file.getAttribute(file.elem, "name")
        child <- file.getSingleChild(file.elem, "members")
        members <- Right((child \ "member").toList)
        arrays <- Result.map(members, array(name) _)
      }
      yield arrays.filter(_.isDefined).map(_.get).map((Nil, _, Nil))
    }

    /** Extracts enums from struct member types */
    def defEnumAnnotatedList(file: XmlFppWriter.File): Result.Result[List[Ast.Annotated[Ast.DefEnum]]] =
      Right(Nil)

    /** Translates the struct type */
    def defStructAnnotated(file: XmlFppWriter.File): Result.Result[Ast.Annotated[Ast.DefStruct]] =
      for {
        comment <- file.getComment
        name <- file.getAttribute(file.elem, "name")
        members <- structTypeMemberAnnotatedList(file)
      }
      yield {
        (comment, Ast.DefStruct(name, members, None), Nil)
      }

    /** Extracts struct type members */
    def structTypeMemberAnnotatedList(file: XmlFppWriter.File): 
      Result.Result[List[Ast.Annotated[AstNode[Ast.StructTypeMember]]]] = 
    {
      // TODO
      Right(Nil)
    }

    /** Extracts a struct type member */
    def structTypeMemberAnnotated(node: scala.xml.Node): 
      Result.Result[Ast.Annotated[AstNode[Ast.StructTypeMember]]] =
    {
      val data = Ast.StructTypeMember("TODO", AstNode.create(Ast.TypeNameBool), None)
      val node = AstNode.create(data)
      val aNode = (Nil, node, Nil)
      Right(aNode)
    }

    /** Generates the list of TU members */
    def tuMemberList(file: XmlFppWriter.File): Result.Result[List[Ast.TUMember]] = {
      for {
        arrays <- defArrayAnnotatedList(file)
        enums <- defEnumAnnotatedList(file)
        struct <- defStructAnnotated(file)
      }
      yield {
        def transformer[A,B](constructor: AstNode[A] => B)(a: Ast.Annotated[A]) = 
          (a._1, constructor(AstNode.create(a._2)), a._3)
        val moduleNames = XmlFppWriter.getAttributeNamespace(file.elem)
        val memberNodes = moduleNames match {
          case Nil => {
            val arrays1 = arrays.map(transformer(Ast.TUMember.DefArray))
            val enums1 = enums.map(transformer(Ast.TUMember.DefEnum))
            val struct1 = transformer(Ast.TUMember.DefStruct)(struct)
            (arrays1 ++ enums1) :+ struct1
          }
          case head :: tail => {
            val arrays1 = arrays.map(transformer(Ast.ModuleMember.DefArray))
            val enums1 = enums.map(transformer(Ast.ModuleMember.DefEnum))
            val struct1 = transformer(Ast.ModuleMember.DefStruct)(struct)
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
