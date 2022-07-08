package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write an FPP format as XML */
object FormatXmlWriter {

  /** Convert a format field and type name to a string */
  def fieldToString(f: Format.Field, tn: AstNode[Ast.TypeName]): String = {
    import Format.Field._
    def default = tn.data match {
      case Ast.TypeNameFloat(name) => "%g"
      case Ast.TypeNameInt(typeInt) => (TypeUtils.width(typeInt), TypeUtils.signedness(typeInt)) match {
        case (64, TypeUtils.Signed) => "%ld"
        case (_, TypeUtils.Signed) => "%d"
        case (64, TypeUtils.Unsigned) => "%lu"
        case (_, TypeUtils.Unsigned) => "%u"
      }
      case Ast.TypeNameQualIdent(name) => "%s"
      case Ast.TypeNameBool => "%d"
      case Ast.TypeNameString(size) => "%s"
    }
    def integer(t: Integer.Type) = tn.data match {
      case Ast.TypeNameInt(typeInt) => (t, TypeUtils.width(typeInt), TypeUtils.signedness(typeInt)) match {
        case (Integer.Decimal, 64, TypeUtils.Signed) => "%ld"
        case (Integer.Decimal, 64, TypeUtils.Unsigned) => "%lu"
        case (Integer.Hexadecimal, 64, _) => "%lx"
        case (Integer.Octal, 64, _) => "%lo"
        case (Integer.Character, 64, _) => default
        case (Integer.Character, _, _) => "%c"
        case (Integer.Decimal, _, TypeUtils.Signed) => "%d"
        case (Integer.Decimal, _, TypeUtils.Unsigned) => "%u"
        case (Integer.Hexadecimal, _, _) => "%x"
        case (Integer.Octal, _, _) => "%o"
      }
      case _ => default
    }
    def rational(precision: Option[Int], t: Rational.Type) = tn.data match {
      case Ast.TypeNameFloat(_) => {
        val precisionStr = precision match {
          case Some(p) => s".${p.toString}"
          case None => ""
        }
        t match {
          case Rational.Exponent => s"%${precisionStr}e"
          case Rational.Fixed => s"%${precisionStr}f"
          case Rational.General => s"%${precisionStr}g"
        }
      }
      case _ => default
    }
    f match {
      case Default => default
      case Integer(t) => integer(t)
      case Rational(precision, t) => rational(precision, t)
    }
  }

  /** Convert a format to a string */
  def formatToString(f: Format, nodes: List[AstNode[Ast.TypeName]]): String = {
    def escapePercent(s: String) = s.replaceAll("%", "%%")
    val fields = f.fields
    if (fields.length != nodes.length) 
      throw new InternalError("number of nodes should match number of fields")
    val pairs = fields zip nodes
    val s0 = escapePercent(f.prefix)
    pairs.foldLeft(s0)({ 
      case (s1, ((field, s2), tn)) => s1 ++ fieldToString(field, tn) ++ escapePercent(s2) 
    })
  }

}
