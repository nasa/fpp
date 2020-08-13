package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.analysis
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out an F Prime XML array as FPP source */
object ArrayXmlFppWriter extends LineUtils {

  def writeFile(file: XmlFppWriter.File): XmlFppWriter.Result =
    for (tuMember <- FppBuilder.tuMember(file))
      yield FppWriter.tuMember(tuMember)

  private object FppBuilder {
   /** Extracts array definitions from struct members */
    def defArrayAnnotated(file: XmlFppWriter.File):
      Result.Result[Ast.Annotated[Ast.DefArray]] =
      for {
        name <- file.getAttribute(file.elem, "name")
        post <- file.getComment
        s <- size(file)
        t <- eltType(file)
      }
      yield {
        val parsedF = parseFormat(file)
        val (f, preF) = formatOrNil(parsedF)
        val parsedD = parseDefaults(file)
        val (d, preD) = defaultsOrNil(parsedD, t.getData)

        (preF ++ preD, Ast.DefArray(name, s, t, d, f), post)
      }

    def size(file: XmlFppWriter.File): Result.Result[AstNode[Ast.Expr]] =
      for {
        node <- file.getSingleChild(file.elem, "size")
      }
      yield {
        val exprNode = Ast.ExprLiteralInt(node.text)
        AstNode.create(exprNode)
      }

    def eltType(file: XmlFppWriter.File): Result.Result[AstNode[Ast.TypeName]] =
      for {
        node <- file.getSingleChild(file.elem, "type")
        typeNode <- translateArrayType(file, node)
      }
      yield {
        AstNode.create(typeNode)
      }

    def formatOrNil(node: Option[scala.xml.Node]): (Option[AstNode[String]], List[String]) = {
      node match {
        case Some(node) => format(node)
        case None => (None, Nil)
      }
    }

    def defaultsOrNil(node: Option[scala.xml.Node], t: Ast.TypeName): (Option[AstNode[Ast.Expr]], List[String]) = {
      node match {
        case Some(node) => defaults(node, t)
        case None => (None, Nil)
      }
    }

    def parseFormat(file: XmlFppWriter.File): Option[scala.xml.Node] = {
      val node = file.getSingleChild(file.elem, "format")

      node match {
        case Right(node) => Some(node)
        case _ => None
      }
    }

    def parseDefaults(file: XmlFppWriter.File): Option[scala.xml.Node] = {
      val node = file.getSingleChild(file.elem, "default")

      node match {
        case Right(node) => Some(node)
        case _ => None
      }
    }

    def format(node: scala.xml.Node): (Option[AstNode[String]], List[String]) = {
      XmlFppWriter.FppBuilder.translateFormatString(node.text) match {
        case Some(f) => (Some(AstNode.create(f)), Nil)
        case None => (None, node.text)
      }
    }

    def defaults(node: scala.xml.Node, typ: Ast.TypeName): (Option[AstNode[Ast.Expr]], List[String]) = {
        val valueNodes = node \ "value"
        val nodesWithTypes = valueNodes.map( (_, typ) )
        val optValues = Options.map(nodesWithTypes.toList, defaultValue)
        optValues.map(exprNodes => AstNode.create(Ast.ExprArray(exprNodes))) match {
          case Some(defaultExpr) => (Some(defaultExpr), Nil)
          case None => (None, List("[" ++ valueNodes.map(_.text).mkString(", ") ++ "]"))
        }
      }

    def defaultValue(value: (scala.xml.Node, Ast.TypeName)): Option[AstNode[Ast.Expr]] = {
      val node = value._1
      val typeName = value._2
      val rawText = node.text
      val exprOpt = typeName match {
        case Ast.TypeNameInt(_) => Some(Ast.ExprLiteralInt(rawText))
        case Ast.TypeNameBool => Some(Ast.ExprLiteralBool(getBool(rawText)))
        case Ast.TypeNameString(_) => Some(Ast.ExprLiteralString(removeQuotes(rawText)))
        case _ => None
      }
      exprOpt.map(AstNode.create(_))
    }

    def removeQuotes(value: String) = {
      val pattern = """\s*".*"\s*""".r
      pattern.matches(value) match {
        case true => value.substring(value.indexOf("\"") + 1, value.lastIndexOf("\""))
        case false => throw new InternalError("Invalid string value in array defaults")
      }
    }

    def getBool(value: String): Ast.LiteralBool = {
      value match {
        case "True" => Ast.LiteralBool.True
        case "False" => Ast.LiteralBool.False
      }
    }

    def tuMember(file: XmlFppWriter.File): Result.Result[Ast.TUMember] =
      for (data <- defArrayAnnotated(file))
      yield {
        def transform[A,B](construct: AstNode[A] => B)(a: Ast.Annotated[A]) = 
          (a._1, construct(AstNode.create(a._2)), a._3)
        val moduleNames = XmlFppWriter.getAttributeNamespace(file.elem)
        val aNode = moduleNames match {
          case Nil => transform(Ast.TUMember.DefArray)(data)
          case head :: tail => {
            val aNodeList1 = List(transform(Ast.TUMember.DefArray)(data))
            val aNodeList2 = XmlFppWriter.FppBuilder.encloseWithModuleMemberModules(tail.reverse)(aNodeList1)
            XmlFppWriter.FppBuilder.encloseWithTuMemberModule(head)(aNodeList2)
          }
        }
        Ast.TUMember(aNode)
      }

    /** This is from XmlFppWriter but changed for array type xml nodes */
    def translateArrayType(file: XmlFppWriter.File, node: scala.xml.Node): Result.Result[Ast.TypeName] = {
      val xmlType = node.text
      for {
        result <- {
          val sizeOpt = XmlFppWriter.getAttributeOpt(node, "size")
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
              enum <- file.getSingleChild(node, "enum")
              name <- file.getAttribute(enum, "name")
            } yield XmlFppWriter.FppBuilder.translateQualIdentType(name)
            case "string" => Right(Ast.TypeNameString(
              sizeOpt.map((size: String) => AstNode.create(Ast.ExprLiteralInt(size)))
            ))
            case _ => Right(XmlFppWriter.FppBuilder.translateQualIdentType(xmlType))
          }
        }
      } yield result
    }

  }
}