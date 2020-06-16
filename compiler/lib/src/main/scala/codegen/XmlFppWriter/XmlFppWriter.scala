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

    def encloseWithTuMemberModule
      (name: String)
      (nodeList: List[Ast.Annotated[Ast.ModuleMember.Node]]): Ast.Annotated[Ast.TUMember.Node] =
      encloseWithModule(Ast.TUMember.DefModule(_))(name)(nodeList)

    def encloseWithModuleMemberModules
      (names: List[String])
      (aNode: Ast.Annotated[Ast.ModuleMember.Node]): Ast.Annotated[Ast.ModuleMember.Node] = {
      def encloseWithModuleMemberModule
        (name: String)
        (aNode: Ast.Annotated[Ast.ModuleMember.Node]): Ast.Annotated[Ast.ModuleMember.Node] =
          encloseWithModule(Ast.ModuleMember.DefModule(_))(name)(List(aNode))
      names match {
        case Nil => aNode
        case head :: tail => encloseWithModuleMemberModules(tail)(encloseWithModuleMemberModule(head)(aNode))
      }
    }

    private def encloseWithModule[T]
      (f: AstNode[Ast.DefModule] => T)
      (name:String)
      (aNodeList: List[Ast.Annotated[Ast.ModuleMember.Node]]): Ast.Annotated[T] = 
    {
      val moduleMembers = aNodeList.map((aNode: Ast.Annotated[Ast.ModuleMember.Node]) => Ast.ModuleMember(aNode))
      val defModule = Ast.DefModule(name, moduleMembers)
      val node1 = AstNode.create(defModule)
      (Nil, f(node1), Nil)
    }

  }

}
