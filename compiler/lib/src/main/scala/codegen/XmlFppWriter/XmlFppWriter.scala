package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes F Prime XML as FPP source */
object XmlFppWriter extends LineUtils {

  type Result = Result.Result[List[Line]]

  /** An F Prime XML file */
  case class File(
    /** The file name */
    name: String,
    /** The XML element */
    elem: scala.xml.Elem
  ) {

    def error(e: (String) => Error) = e(name)

    def getAttribute(node: scala.xml.Node, name: String): Result.Result[String] = 
      getAttributeOpt(node, name) match {
        case Some(s) => Right(s)
        case None => Left(error(XmlError.SemanticError(_, s"missing attribute $name for node ${node.toString}")))
      }

    def getComment: Result.Result[List[String]] =
      for (childOpt <- getSingleChildOpt(elem, "comment"))
        yield {
          def removeOuterBlanks(list: List[String]) = {
            def removeLeadingBlanks(list: List[String]): List[String] =
              list match {
                case "" :: tail => removeLeadingBlanks(tail)
                case _ => list
              }
            def removeTrailingBlanks(list: List[String]) =
              removeLeadingBlanks(list.reverse).reverse
            removeTrailingBlanks(removeLeadingBlanks(list))
          }
          childOpt match {
            case Some(node) => {
              val list = node.child.map(_.toString.split("\n").map(_.trim)).flatten.toList
              removeOuterBlanks(list)
            }
            case None => Nil
          }
        }

    def getSingleChild(node: scala.xml.Node, name: String): Result.Result[scala.xml.Node] =
      getSingleChildOpt(node, name) match {
        case Right(Some(child)) => Right(child)
        case Right(None) => Left(error(XmlError.SemanticError(_, s"missing child $name for node ${node.toString}")))
        case Left(e) => Left(e)
      }

    def getSingleChildOpt(node: scala.xml.Node, name: String): Result.Result[Option[scala.xml.Node]] =
      (node \ name).toList match {
        case head :: Nil => Right(Some(head))
        case Nil => Right(None)
        case _ => Left(error(XmlError.SemanticError(_, s"multiple child nodes $name for node ${node.toString}")))
      }

    def write: Result = {
      val eltType = elem.label
      for {
        body <- eltType match {
          case "enum" => EnumXmlFppWriter.writeFile(this)
          case "serializable" => StructXmlFppWriter.writeFile(this)
          case _ => Left(error(XmlError.SemanticError(_, s"invalid element type $eltType")))
        }
      }
      yield body
    }

  }

  def getAttributeComment(node: scala.xml.Node): List[String] =
    getAttributeOpt(node, "comment") match {
      case Some(s) => s.split("\n").map(_.trim).toList
      case None => Nil
    }

  def getAttributeNamespace(node: scala.xml.Node): List[String] =
    getAttributeOpt(node, "namespace") match {
      case Some(s) => s.split("::").toList
      case None => Nil
    }

  def getAttributeOpt(node: scala.xml.Node, name: String): Option[String] = 
    node.attribute(name).map(_.toList.head.toString)
  
  def writeFileList(fileList: List[File]) = {
    for (files <- Result.map(fileList, (file: File) => file.write))
      yield Line.blankSeparated (identity[List[Line]]) (files)
  }

  object FppBuilder {

    /** Enclose a list of module members with a module inside a trans unit */
    def encloseWithTuMemberModule
      (name: String)
      (members: List[Ast.Annotated[Ast.ModuleMember.Node]]): 
      Ast.Annotated[Ast.TUMember.Node] =
        encloseWithModule(Ast.TUMember.DefModule(_))(name)(members)

    /** Enclose a list of module members with zero more modules inside a module */
    def encloseWithModuleMemberModules
      (names: List[String])
      (members: List[Ast.Annotated[Ast.ModuleMember.Node]]):
      List[Ast.Annotated[Ast.ModuleMember.Node]] =
    {
      def encloseWithModuleMemberModule
        (name: String)
        (members: List[Ast.Annotated[Ast.ModuleMember.Node]]):
        List[Ast.Annotated[Ast.ModuleMember.Node]] =
          List(encloseWithModule(Ast.ModuleMember.DefModule(_))(name)(members))
      names match {
        case Nil => members
        case head :: tail => encloseWithModuleMemberModules(tail)(encloseWithModuleMemberModule(head)(members))
      }
    }

    /** Translates an XML type to an FPP type name */
    def translateType(xmlType: String, size: Option[String]): Ast.TypeName = xmlType match {
      case "I16" => Ast.TypeNameInt(Ast.I16())
      case "I32" => Ast.TypeNameInt(Ast.I32())
      case "I64" => Ast.TypeNameInt(Ast.I64())
      case "I8" => Ast.TypeNameInt(Ast.I8())
      case "F32" => Ast.TypeNameFloat(Ast.F32())
      case "F64" => Ast.TypeNameFloat(Ast.F64())
      case "U16" => Ast.TypeNameInt(Ast.U16())
      case "U32" => Ast.TypeNameInt(Ast.U32())
      case "U64" => Ast.TypeNameInt(Ast.U64())
      case "U8" => Ast.TypeNameInt(Ast.U8())
      case "bool" => Ast.TypeNameBool
      case "string" => Ast.TypeNameString(
        size.map((s: String) => AstNode.create(Ast.ExprLiteralInt(s)))
      )
      case _ => Ast.TypeNameQualIdent(
        AstNode.create(Ast.QualIdent.fromNodeList(xmlType.split("::").toList.map(AstNode.create(_))))
      )
    }

    /** Encloses several member nodes with a module of variant type */
    private def encloseWithModule[MemberType]
      (memberTypeConstructor: AstNode[Ast.DefModule] => MemberType)
      (name:String)
      (memberNodes: List[Ast.Annotated[Ast.ModuleMember.Node]]):
      Ast.Annotated[MemberType] = 
    {
      val members = memberNodes.map(Ast.ModuleMember(_))
      val defModule = Ast.DefModule(name, members)
      val node = AstNode.create(defModule)
      (Nil, memberTypeConstructor(node), Nil)
    }

  }

}
