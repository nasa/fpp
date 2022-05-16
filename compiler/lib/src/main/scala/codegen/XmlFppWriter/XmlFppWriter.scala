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
    def error(e: (String) => Error): Error = e(name)

    /** Constructs a semantic error with the given message */
    def semanticError(message: String): Error = error(XmlError.SemanticError(_, message))

    /** Gets an attribute from a node, returning an error if it is not there */
    def getAttribute(node: scala.xml.Node, name: String): Result.Result[String] = 
      getAttributeOpt(node, name) match {
        case Some(s) => Right(s)
        case None => Left(semanticError(s"missing attribute $name for node ${node.toString}"))
      }

    /** Gets a comment from a node, returning an empty list if it is not there */
    def getComment(node: scala.xml.Node): Result.Result[List[String]] =
      for (childOpt <- getSingleChildOpt(node, "comment"))
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
              semanticError(s"invalid value $value for node ${node.toString}")
            )
          }
        case None => Right(defaultValue)
      }

    /** Gets a single named child from a node, returning an error if it is not there */
    def getSingleChild(node: scala.xml.Node, name: String): Result.Result[scala.xml.Node] =
      getSingleChildOpt(node, name) match {
        case Right(Some(child)) => Right(child)
        case Right(None) => Left(semanticError(s"missing child $name for node ${node.toString}"))
        case Left(e) => Left(e)
      }

    /** Gets an optional single named child from a node */
    def getSingleChildOpt(node: scala.xml.Node, name: String): Result.Result[Option[scala.xml.Node]] =
      (node \ name).toList match {
        case head :: Nil => Right(Some(head))
        case Nil => Right(None)
        case _ => Left(semanticError(s"multiple child nodes $name for node ${node.toString}"))
      }

    /** Gets the unique child of a node */
    def getUniqueChild(node: scala.xml.Node): Result.Result[scala.xml.Node] =
      node.child.size match {
        case 0 => Left(semanticError(s"missing child for node ${node.toString}"))
        case 1 => Right(node.child.head)
        case _ => Left(semanticError(s"too many children of node ${node.toString}"))
      }

    /** Reports an invalid attribute */
    def invalidAttribute(name: String, node: scala.xml.Node): Error =
      semanticError(s"invalid attribute $name in node ${node.toString}")

    /** Translates an XML type to an FPP type name */
    def translateType
      (getType: scala.xml.Node => Result.Result[String])
      (node: scala.xml.Node): Result.Result[Ast.TypeName] =
      for {
        xmlType <- getType(node)
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
              enumeration <- getSingleChild(node, "enum")
              name <- getAttribute(enumeration, "name")
            } yield FppBuilder.translateQualIdentType(name)
            case "string" => Right(Ast.TypeNameString(
              sizeOpt.map((size: String) => AstNode.create(Ast.ExprLiteralInt(size)))
            ))
            case _ => Right(FppBuilder.translateQualIdentType(xmlType))
          }
        }
      } yield result

    /** Writes a file as lines */
    def write: Result = {
      val eltType = elem.label
      for {
        body <- eltType match {
          case "array" => ArrayXmlFppWriter.writeFile(this)
          case "assembly" => TopologyXmlFppWriter.writeFile(this)
          case "commands" => ComponentXmlFppWriter.writeCommandsFile(this)
          case "component" => ComponentXmlFppWriter.writeComponentFile(this)
          case "enum" => EnumXmlFppWriter.writeFile(this)
          case "events" => ComponentXmlFppWriter.writeEventsFile(this)
          case "interface" => PortXmlFppWriter.writeFile(this)
          case "internal_interfaces" => ComponentXmlFppWriter.writeInternalPortsFile(this)
          case "parameters" => ComponentXmlFppWriter.writeParamsFile(this)
          case "ports" => ComponentXmlFppWriter.writePortsFile(this)
          case "serializable" => StructXmlFppWriter.writeFile(this)
          case "telemetry" => ComponentXmlFppWriter.writeTlmChannelsFile(this)
          case _ => Left(semanticError(s"invalid element type $eltType"))
        }
      }
      yield body
    }

  }

  /** Constructs a translator note */
  def constructNote(s: String): String = "FPP from XML: " ++ s

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
  
  /** Writes a file list */
  def writeFileList(fileList: List[File]): Result.Result[List[Line]] = {
    for (files <- Result.map(fileList, (file: File) => file.write))
      yield Line.blankSeparated (identity[List[Line]]) (files)
  }

  /** Builds a TU member from a single annotated element */
  def tuMember[T](
    aT: Ast.Annotated[T],
    tumConstructor: AstNode[T] => Ast.TUMember.Node,
    moduleConstructor: AstNode[T] => Ast.ModuleMember.Node,
    file: XmlFppWriter.File
  ): Ast.TUMember = tuMemberList(
    Nil: List[Ast.Annotated[Ast.DefEnum]],
    Ast.TUMember.DefEnum.apply,
    Ast.ModuleMember.DefEnum.apply,
    aT,
    tumConstructor,
    moduleConstructor,
    file
  ).head

  /** Transforms an annotated AST node */
  def transformNode[A,B](transform: AstNode[A] => B)(a: Ast.Annotated[A]): (List[String], B, List[String]) = 
    (a._1, transform(AstNode.create(a._2)), a._3)

  /** Builds a list of TU members from a list of annotated A elements
   *  followed by a single annotated B element */
  def tuMemberList[A,B](
    aNodesA: List[Ast.Annotated[A]],
    tumConstructorA: AstNode[A] => Ast.TUMember.Node,
    moduleConstructorA: AstNode[A] => Ast.ModuleMember.Node,
    aNodeB: Ast.Annotated[B],
    tumConstructorB: AstNode[B] => Ast.TUMember.Node,
    moduleConstructorB: AstNode[B] => Ast.ModuleMember.Node,
    file: XmlFppWriter.File,
  ): List[Ast.TUMember] = {
    val moduleNames = XmlFppWriter.getAttributeNamespace(file.elem)
    val memberNodes = moduleNames match {
      case Nil => {
        // Generate a list of TU members
        val aNodesA1 = aNodesA.map(transformNode(tumConstructorA))
        val aNodeB1 = transformNode(tumConstructorB)(aNodeB)
        aNodesA1 :+ aNodeB1
      }
      case head :: tail => {
        // Generate a TU member consisting of one or more modules enclosing module members
        val aNodesA1 = aNodesA.map(transformNode(moduleConstructorA))
        val aNodeB1 = transformNode(moduleConstructorB)(aNodeB)
        val members = aNodesA1 :+ aNodeB1
        val members1 = XmlFppWriter.FppBuilder.encloseWithModuleMemberModules(tail.reverse)(members)
        List(XmlFppWriter.FppBuilder.encloseWithTuMemberModule(head)(members1))
      }
    }
    memberNodes.map(Ast.TUMember(_))
  }      

  /** Utilities for constructing FPP ASTs */
  object FppBuilder {

    /** Encloses a list of module members with a module inside a trans unit */
    def encloseWithTuMemberModule
      (name: String)
      (members: List[Ast.Annotated[Ast.ModuleMember.Node]]): 
      Ast.Annotated[Ast.TUMember.Node] =
        encloseWithModule(Ast.TUMember.DefModule.apply)(name)(members)

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
          List(encloseWithModule(Ast.ModuleMember.DefModule.apply)(name)(members))
      names match {
        case Nil => members
        case head :: tail => encloseWithModuleMemberModules(tail)(
          encloseWithModuleMemberModule(head)(members)
        )
      }
    }

    /** Translates a qualified identifier type */
    def translateQualIdentType(xmlType: String): Ast.TypeNameQualIdent = 
      Ast.TypeNameQualIdent(translateQualIdent(xmlType))

    /** Translates a qualified identifier */
    def translateQualIdent(xmlQid: String): AstNode[Ast.QualIdent] = 
      AstNode.create(
        Ast.QualIdent.fromNodeList(
          xmlQid.split("::").toList.map(AstNode.create(_))
        )
      )

    /** Translates an XML format */
    def translateFormat(xmlFormat: String): Option[String] = {
      val repls = List(
        ("\\{" -> "{{"),
        ("\\}" -> "}}"),
        ("(%ld|%d|%lu|%u|%s|%g|%llu|%lld)" -> "{}"),
        ("%c" -> "{c}"),
        ("(%o|%lo|%llo)" ->"{o}"),
        ("(%x|%lx|%llx)" -> "{x}"),
        ("%e"-> "{e}"),
        ("%f"-> "{f}"),
        ("%(\\.[0-9]+)e"-> "{$1e}"),
        ("%(\\.[0-9]+)f"-> "{$1f}"),
        ("%(\\.[0-9]+)g" -> "{$1g}")
      )
      val s = repls.foldLeft(xmlFormat)({ case (s, (a, b)) => s.replaceAll(a, b) })
      if (!s.replaceAll("%%", "").contains("%"))
        Some(s.replaceAll("%%", "%")) else None
    }

    /** Translates an optional XML format.
     *  Returns the translated format and a note. */
    def translateFormatOpt(xmlFormatOpt: Option[String]): (Option[String], List[String]) = {
      val fppFormatOpt = xmlFormatOpt.flatMap(FppBuilder.translateFormat(_))
      val note = (xmlFormatOpt, fppFormatOpt) match {
        case (Some(xmlFormat), None) =>
          val s = "could not translate format string \"" ++ xmlFormat ++ "\""
          List(constructNote(s))
        case _ => Nil
      }
      // Represent default format "{}" more succinctly as no format
      val format = fppFormatOpt match {
        case Some("{}") => None
        case _ => fppFormatOpt
      }
      (format, note)
    }

    /** Translates a value from FPP to XML */
    def translateValue(xmlValue: String, tn: Ast.TypeName): Option[AstNode[Ast.Expr]] = {
      val exprOpt = (xmlValue, tn) match {
        case (_, Ast.TypeNameInt(_)) => Some(Ast.ExprLiteralInt(xmlValue))
        case (_, Ast.TypeNameFloat(_)) => Some(Ast.ExprLiteralFloat(xmlValue))
        case ("true", Ast.TypeNameBool) => Some(Ast.ExprLiteralBool(Ast.LiteralBool.True))
        case ("false", Ast.TypeNameBool) => Some(Ast.ExprLiteralBool(Ast.LiteralBool.False))
        case (_, Ast.TypeNameString(_)) => Some(Ast.ExprLiteralString(xmlValue.replaceAll("^\"|\"$", "")))
        case _ => 
          if ("[^A-Za-z0-9_:]".r.findAllIn(xmlValue).length > 0)
            // Not a qualified identifier -- don't translate
            None
          else {
            // C++ qualified identifier. Translate to an FPP qualified identifier.
            val head :: tail = xmlValue.split("::").toList
            val e = tail.foldLeft (Ast.ExprIdent(head): Ast.Expr) ((e1, s) =>
              Ast.ExprDot(AstNode.create(e1), AstNode.create(s))
            )
            Some(e)
          }
      }
      exprOpt.map(AstNode.create(_))
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
