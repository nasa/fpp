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
        d <- file.getSingleChild(file.elem, "default")
        f <- file.getSingleChild(file.elem, "format")
      }
      yield {
        val (fValue, preF) = format(f)
        val (dValue, preD) = defaults((d, t.getData))
        (preD ++ preF, Ast.DefArray(name, s, t, dValue, fValue), post)
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
        typeNode <- file.translateArrayType(node)
        //typeNode <- file.translateType(node)
      }
      yield AstNode.create(typeNode)

    def format(node: scala.xml.Node): (Option[AstNode[String]], List[String]) = {
      XmlFppWriter.FppBuilder.translateFormatString(node.text) match {
        case Some(f) => (Some(AstNode.create(f)), Nil)
        case None => (None, List(XmlFppWriter.constructNote(node.text)))
      }
    }

    def defaults(value: (scala.xml.Node, Ast.TypeName)): (Option[AstNode[Ast.Expr]], List[String]) = {
      val node = value._1
      val typ = value._2
      val valueNodes = node \ "value"
      val nodesWithTypes = valueNodes.map( (_, typ) )
      val optValues = Options.map(nodesWithTypes.toList, defaultValue)
      optValues.map(exprNodes => AstNode.create(Ast.ExprArray(exprNodes))) match {
        case Some(defaultExpr) => (Some(defaultExpr), Nil)
        case None => (None, List(XmlFppWriter.constructNote("[" ++ valueNodes.map(_.text).mkString(", ") ++ "]")))
      }
    }

    def defaultValue(value: (scala.xml.Node, Ast.TypeName)): Option[AstNode[Ast.Expr]] = {
      val node = value._1
      val typeName = value._2
      val text = node.text.replaceAll("^\"|\"$", "")
      val exprOpt = typeName match {
        case Ast.TypeNameInt(_) => Some(Ast.ExprLiteralInt(text))
        case Ast.TypeNameBool => Some(Ast.ExprLiteralBool(getBool(text)))
        case Ast.TypeNameString(_) => Some(Ast.ExprLiteralString(text))
        case _ => None
      }
      exprOpt.map(AstNode.create(_))
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

  }

}
