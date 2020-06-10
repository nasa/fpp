package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.util._
import scala.language.implicitConversions

/** Write out FPP source */
object FppWriter extends AstVisitor with LineUtils {

  private case class JoinOps(ls: List[Line]) {

    def addSuffix(suffix: String) = Line.addSuffix(ls, suffix)

    def join (sep: String) (ls1: List[Line]) = Line.joinLists (Line.Indent) (ls) (sep) (ls1)

    def joinOpt[T] (opt: Option[T]) (sep: String) (f: T => List[Line]) =
      opt match {
        case Some(t) => join (sep) (f(t))
        case None => ls
      }

  }

  private implicit def lift(ls: List[Line]) = JoinOps(ls)

  def transUnit(tu: Ast.TransUnit): Out = transUnit((), tu)

  override def defAbsTypeAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]) = {
    val (_, node, _) = aNode
    val data = node.data
    lines(s"type ${data.name}")
  }

  override def defArrayAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines(s"array ${data.name} = [").
      join ("") (exprNode(data.size)).
      join ("] ") (typeNameNode(data.eltType)).
      joinOpt (data.default) (" default ") (exprNode).
      joinOpt (data.format) (" format ") (applyToData(string))
  }

  override def defConstantAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines(s"constant ${data.name}").join (" = ") (exprNode(data.value))
  }

  override def defEnumAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines(s"enum ${data.name}").
      joinOpt (data.typeName) (": ") (typeNameNode).
      addSuffix(" {") ++
    data.constants.map(annotateNode(defEnumConstant)).flatten.map(indentIn) ++
    lines("}")
  }

  override def defModuleAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefModule]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    List(line(s"module ${data.name} {"), Line.blank) ++
    (Line.blankSeparated (moduleMember) (data.members)).map(indentIn) ++
    List(Line.blank, line("}"))
  }

  override def defStructAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines(s"struct ${data.name} {") ++
    data.members.map(annotateNode(structTypeMember)).flatten.map(indentIn) ++ 
    lines("}").joinOpt (data.default) (" default ") (exprNode)
  }

  override def default(in: Unit) = Nil

  override def exprArrayNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprArray) =
    (line("[") :: e.elts.map(exprNode).flatten.map(indentIn)) :+ line("]")

  override def exprBinopNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprBinop) =
    exprNode(e.e1).join (binop(e.op)) (exprNode(e.e2))
  
  override def exprDotNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprDot) =
    exprNode(e.e).join (".") (lines(e.id.getData))

  override def exprIdentNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprIdent) = 
    lines(e.value)

  override def exprLiteralBoolNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralBool) = {
    val s = e.value match {
      case Ast.LiteralBool.True => "true"
      case Ast.LiteralBool.False => "false"
    }
    lines(s)
  }

  override def exprLiteralFloatNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralFloat) =
    lines(e.value)
  
  override def exprLiteralIntNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralInt) =
    lines(e.value)

  override def exprLiteralStringNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprLiteralString) =
    string(e.value)

  override def exprParenNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprParen) =
    Line.addPrefixAndSuffix("(", exprNode(e.e), ")")

  override def exprStructNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprStruct) =
    lines("{") ++
    e.members.map(applyToData(structMember)).flatten.map(indentIn) ++
    lines("}")

  override def exprUnopNode(in: Unit, node: AstNode[Ast.Expr], e: Ast.ExprUnop) =
    lines(unop(e.op)).join ("") (exprNode(e.e))

  override def specIncludeAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.SpecInclude]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    lines("include").join (" ") (string(data.file.getData))
  }

  override def specLocAnnotatedNode(in: Unit, aNode: Ast.Annotated[AstNode[Ast.SpecLoc]]) = {
    val (_, node, _) = aNode
    val data = node.getData
    val kind = data.kind match {
      case Ast.SpecLoc.Component => "component"
      case Ast.SpecLoc.ComponentInstance => "component instance"
      case Ast.SpecLoc.Constant => "constant"
      case Ast.SpecLoc.Port => "port"
      case Ast.SpecLoc.Topology => "topology"
      case Ast.SpecLoc.Type => "type"
    }
    lines(s"locate ${kind}").
      join (" ") (qualIdent(data.symbol.getData)).
      join (" at ") (string(data.file.getData))
  }

  override def transUnit(in: Unit, tu: Ast.TransUnit) =
    Line.blankSeparated (tuMember) (tu.members)

  override def typeNameBoolNode(in: Unit, node: AstNode[Ast.TypeName]) = lines("bool")

  override def typeNameFloatNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameFloat) = {
    val s = tn.name match {
      case _: Ast.F32 => "F32"
      case _: Ast.F64 => "F64"
    }
    lines(s)
  }

  override def typeNameIntNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameInt) = {
    val s = tn.name match {
      case _: Ast.I8 => "I8"
      case _: Ast.I16 => "I16"
      case _: Ast.I32 => "I32"
      case _: Ast.I64 => "I64"
      case _: Ast.U8 => "U8"
      case _: Ast.U16 => "U16"
      case _: Ast.U32 => "U32"
      case _: Ast.U64 => "U64"
    }
    lines(s)
  }

  override def typeNameQualIdentNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameQualIdent) = 
    qualIdent(tn.name.getData)

  override def typeNameStringNode(in: Unit, node: AstNode[Ast.TypeName], tn: Ast.TypeNameString) =
    lines("string").joinOpt (tn.size) (" size ") (exprNode)

  private def annotate(pre: List[String], lines: List[Line], post: List[String]) = {
    val pre1 = pre.map((s: String) => line("@ " ++ s))
    val post1 = post.map((s: String) => line("@< " ++ s))
    (pre1 ++ lines).join (" ") (post1)
  }

  private def annotateNode[T](f: T => List[Line]): Ast.Annotated[AstNode[T]] => List[Line] =
    (aNode: Ast.Annotated[AstNode[T]]) => {
      val (a1, node, a2) = aNode
      annotate(a1, f(node.getData), a2)
    }

  private def applyToData[A,B](f: A => B): AstNode[A] => B = 
    (a: AstNode[A]) => f(a.getData)

  private def binop(op: Ast.Binop) = op match {
    case Ast.Binop.Add => " + "
    case Ast.Binop.Div => " / "
    case Ast.Binop.Mul => " * "
    case Ast.Binop.Sub => " - "
  }

  private def defEnumConstant(dec: Ast.DefEnumConstant) =
    lines(dec.name).joinOpt (dec.value) (" = ") (exprNode)

  private def exprNode(node: AstNode[Ast.Expr]): List[Line] = matchExprNode((), node)

  private def moduleMember(member: Ast.ModuleMember) = {
    val (a1, _, a2) = member.node
    val l = matchModuleMember((), member)
    annotate(a1, l, a2)
  }

  private def qualIdent(qid: Ast.QualIdent): List[Line] =
    lines(qualIdentString(qid))

  private def qualIdentString(qid: Ast.QualIdent): String =
    qid match {
      case Ast.QualIdent.Unqualified(name) => name
      case Ast.QualIdent.Qualified(qualifier, name) => 
        qualIdentString(qualifier.getData) ++ "." ++ name.getData
    }

  private def string(s: String) = s.split("\n").toList match {
    case Nil => lines("\"\"")
    case s :: Nil => lines("\"" ++ s.replaceAll("\"", "\\\"") ++ "\"")
    case ss => {
      lines("\"\"\"") ++
      ss.map((s: String) => line(s.replaceAll("\"\"\"", "\\\"\"\""))) ++
      lines("\"\"\"")
    }
  }

  private def structMember(member: Ast.StructMember) =
    lines(member.name).join (" = ") (exprNode(member.value))

  private def structTypeMember(member: Ast.StructTypeMember) =
    lines(member.name).
      join (": ") (typeNameNode(member.typeName)).
      joinOpt (member.format) (" format ") (applyToData(string))

  private def tuMember(tum: Ast.TUMember) = moduleMember(tum)

  private def typeNameNode(node: AstNode[Ast.TypeName]) = matchTypeNameNode((), node)

  private def unop(op: Ast.Unop) =
    op match {
      case Ast.Unop.Minus => "-"
    }

  type In = Unit

  type Out = List[Line]

}
