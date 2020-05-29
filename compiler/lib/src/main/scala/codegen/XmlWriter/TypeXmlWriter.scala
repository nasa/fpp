package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.util._

/** Write an FPP type as XML */
object TypeXmlWriter {

  private object Visitor extends AstVisitor {

    override def default(u: Unit) = throw new InternalError("visitor not defined")

    override def typeNameBoolNode(u: Unit, node: AstNode[Ast.TypeName]) = "boolean"

    override def typeNameFloatNode(u: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat) =
      tn.name match {
        case Ast.F32() => "F32"
        case Ast.F64() => "F64"
      }

    override def typeNameIntNode(u: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt) =
      tn.name match {
        case Ast.I8() => "I8"
        case Ast.I16() => "I16"
        case Ast.I32() => "I32"
        case Ast.I64() => "I64"
        case Ast.U8() => "U8"
        case Ast.U16() => "U16"
        case Ast.U32() => "U32"
        case Ast.U64() => "U64"
      }

    override def typeNameQualIdentNode(u: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent) = {
      def qualIdent(qi: Ast.QualIdent): String = qi match {
        case Ast.QualIdent.Unqualified(name) => name
        case Ast.QualIdent.Qualified(qualifier, name) => s"${qualIdent(qualifier.getData)}::${name.getData}"
      }
      qualIdent(tn.name.getData)
    }

    override def typeNameStringNode(u: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameString) = "string"

    type In = Unit

    type Out = String
  }

  /** Get the name of a type */
  def getName(s: XmlWriter.State, tn: AstNode[Ast.TypeName]): String = Visitor.matchTypeNameNode((), tn)

  /** Get the size of a type */
  def getSize(s: XmlWriter.State, tn: AstNode[Ast.TypeName]): Option[String] = tn.getData match {
    case Ast.TypeNameString(Some(node)) => Some(s.a.valueMap(node.getId).toString)
    case Ast.TypeNameString(_) => Some(s.defaultStringSize.toString)
    case _ => None
  }

  /** Get the key-value pairs for a type */
  def getPairs(s: XmlWriter.State, tn: AstNode[Ast.TypeName]): List[(String,String)] = {
    val name = ("type", getName(s, tn))
    getSize(s, tn) match {
      case Some(size) => List(name, ("size", size))
      case None => List(name)
    }
  }

}
