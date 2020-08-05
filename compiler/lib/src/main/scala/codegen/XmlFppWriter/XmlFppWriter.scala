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

    /** Constructs an error containing the file name */
    def error(e: (String) => Error) = e(name)

    /** Gets an attribute from a node, returning an error if it is not there */
    def getAttribute(node: scala.xml.Node, name: String): Result.Result[String] = 
      getAttributeOpt(node, name) match {
        case Some(s) => Right(s)
        case None => Left(
          error(XmlError.SemanticError(_, s"missing attribute $name for node ${node.toString}"))
        )
      }

    /** Gets a comment from a node, returning an empty list if it is not there */
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

    /** Gets the integer value attribute from a node */
    def getIntegerValueAttribute(defaultValue: Int)(node: scala.xml.Node):
      Result.Result[Int]= 
      getAttributeOpt(node, "value") match {
        case Some(value) => 
          try { 
            Right(value.toInt) 
          } catch {
            case _: Exception => Left(
              error(XmlError.SemanticError(_, s"invalid value $value for node ${node.toString}"))
            )
          }
        case None => Right(defaultValue)
      }

    /** Gets a single child from a node, returning an error if it is not there */
    def getSingleChild(node: scala.xml.Node, name: String): Result.Result[scala.xml.Node] =
      getSingleChildOpt(node, name) match {
        case Right(Some(child)) => Right(child)
        case Right(None) => Left(error(XmlError.SemanticError(_, s"missing child $name for node ${node.toString}")))
        case Left(e) => Left(e)
      }

    /** Gets an optional single child from a node */
    def getSingleChildOpt(node: scala.xml.Node, name: String): Result.Result[Option[scala.xml.Node]] =
      (node \ name).toList match {
        case head :: Nil => Right(Some(head))
        case Nil => Right(None)
        case _ => Left(error(XmlError.SemanticError(_, s"multiple child nodes $name for node ${node.toString}")))
      }

    /** Translates an XML type to an FPP type name */
    def translateType(node: scala.xml.Node): Result.Result[Ast.TypeName] = {
      for {
        xmlType <- getAttribute(node, "type")
        result <- {
          val sizeOpt = getAttributeOpt(node, "size")
          xmlType match {
            case "I16" => Right(Ast.TypeNameInt(Ast.I16()))
            case "I32" => Right(Ast.TypeNameInt(Ast.I32()))
            case "I64" => Right(Ast.TypeNameInt(Ast.I64()))
            case "I8" => Right(Ast.TypeNameInt(Ast.I8()))
            case "F32" => Right(Ast.TypeNameFloat(Ast.F32()))
            case "F64" => Right(Ast.TypeNameFloat(Ast.F64()))
            case "U16" => Right(Ast.TypeNameInt(Ast.U16()))
            case "U32" => Right(Ast.TypeNameInt(Ast.U32()))
            case "U64" => Right(Ast.TypeNameInt(Ast.U64()))
            case "U8" => Right(Ast.TypeNameInt(Ast.U8()))
            case "bool" => Right(Ast.TypeNameBool)
            case "ENUM" => for {
              enum <- getSingleChild(node, "enum")
              name <- getAttribute(enum, "name")
            } yield FppBuilder.translateQualIdentType(name)
            case "string" => Right(Ast.TypeNameString(
              sizeOpt.map((size: String) => AstNode.create(Ast.ExprLiteralInt(size)))
            ))
            case _ => Right(FppBuilder.translateQualIdentType(xmlType))
          }
        }
      } yield result
    }

    /** Writes a file as lines */
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

  /** Constructs a translator note */
  def constructNote(s: String) = "FPP from XML: " ++ s

  /** Gets an attribute comment */
  def getAttributeComment(node: scala.xml.Node): List[String] =
    getAttributeOpt(node, "comment") match {
      case Some(s) => s.split("\n").map(_.trim).toList
      case None => Nil
    }

  /** Gets an attribute namespace */
  def getAttributeNamespace(node: scala.xml.Node): List[String] =
    getAttributeOpt(node, "namespace") match {
      case Some(s) => s.split("::").toList
      case None => Nil
    }

  /** Gets an optional attribute */
  def getAttributeOpt(node: scala.xml.Node, name: String): Option[String] = 
    node.attribute(name).map(_.toList.head.toString)
  
  /** Translates an XML format.
   *  Returns the translated format and a note. */
  def translateFormat(node: scala.xml.Node): (Option[String], List[String]) = {
    val xmlFormatOpt = XmlFppWriter.getAttributeOpt(node, "format")
    val fppFormatOpt = xmlFormatOpt.flatMap(FppBuilder.translateFormatString(_))
    val note = (xmlFormatOpt, fppFormatOpt) match {
      case (Some(xmlFormat), None) => {
        val s = "could not translate format string \"" ++ xmlFormat ++ "\""
        List(constructNote(s))
      }
      case _ => Nil
    }
    (fppFormatOpt, note)
  }

  /** Writes a file list */
  def writeFileList(fileList: List[File]) = {
    for (files <- Result.map(fileList, (file: File) => file.write))
      yield Line.blankSeparated (identity[List[Line]]) (files)
  }

  /** Utilities for constructing FPP ASTs */
  object FppBuilder {

    /** Encloses a list of module members with a module inside a trans unit */
    def encloseWithTuMemberModule
      (name: String)
      (members: List[Ast.Annotated[Ast.ModuleMember.Node]]): 
      Ast.Annotated[Ast.TUMember.Node] =
        encloseWithModule(Ast.TUMember.DefModule(_))(name)(members)

    /** Encloses a list of module members with zero more modules inside a module */
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
        case head :: tail => encloseWithModuleMemberModules(tail)(
          encloseWithModuleMemberModule(head)(members)
        )
      }
    }

    /** Translates a qualified identifier type */
    def translateQualIdentType(xmlType: String) = Ast.TypeNameQualIdent(
      AstNode.create(Ast.QualIdent.fromNodeList(xmlType.split("::").toList.map(AstNode.create(_))))
    )

    /** Translates a format string */
    def translateFormatString(xmlFormat: String): Option[String] = {
      // TODO as part of GitHub issue #43
      None
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

    /** Extracts inline enums from XML nodes */
    object InlineEnumBuilder {

      /** Translates an enum */
      def defEnumAnnotated(file: XmlFppWriter.File)(node: scala.xml.Node):
        Result.Result[Ast.Annotated[Ast.DefEnum]] =
        for {
          name <- file.getAttribute(node, "name")
          constants <- defEnumConstantNodeAnnotatedList(file)(node)
        }
        yield (Nil, Ast.DefEnum(name, None, constants, None), Nil)

      /** Translates an enum if present in the node */
      def defEnumAnnotatedOpt(file: XmlFppWriter.File)(node: scala.xml.Node):
        Result.Result[Option[Ast.Annotated[Ast.DefEnum]]] =
        for {
          enumNodeOpt <- file.getSingleChildOpt(node, "enum")
          result <- enumNodeOpt match {
            case Some(enumNode) => defEnumAnnotated(file)(enumNode).map(Some(_))
            case None => Right(None)
          }
        } yield result

      /** Translates an enum constant node */
      def defEnumConstantNodeAnnotated
        (file: XmlFppWriter.File)
        (defaultValue: Int)
        (node: scala.xml.Node):
        Result.Result[(Ast.Annotated[AstNode[Ast.DefEnumConstant]], Int)] =
      {
        for {
          name <- file.getAttribute(node, "name")
          value <- file.getIntegerValueAttribute(defaultValue)(node)
        }
        yield {
          val data = Ast.DefEnumConstant(
            name,
            Some(AstNode.create(Ast.ExprLiteralInt(value.toString)))
          )
          val astNode = AstNode.create(data)
          val comment = XmlFppWriter.getAttributeComment(node)
          ((Nil, astNode, comment), value + 1)
        }
      }

      /** Translates a list of enum constant nodes */
      def defEnumConstantNodeAnnotatedList
        (file: XmlFppWriter.File)
        (enumNode: scala.xml.Node):
        Result.Result[List[Ast.Annotated[AstNode[Ast.DefEnumConstant]]]] = 
      {
        val items = (enumNode \ "item").toList
        def fold(
          nodes: List[scala.xml.Node],
          defaultValue: Int,
          out: List[Ast.Annotated[AstNode[Ast.DefEnumConstant]]]
        ): Result.Result[List[Ast.Annotated[AstNode[Ast.DefEnumConstant]]]] = {
          nodes match {
            case Nil => Right(out.reverse)
            case head :: tail => defEnumConstantNodeAnnotated(file)(defaultValue)(head) match {
              case Left(e) => Left(e)
              case Right((aNode, nextDefaultValue)) => fold(tail, nextDefaultValue, aNode :: out)
            }
          }
        }
        fold(items, 0, Nil)
      }

    }

  }

}
