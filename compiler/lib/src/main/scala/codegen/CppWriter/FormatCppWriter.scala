package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write an FPP format as C++
 *  Uses PRI macros from cinttypes header for formatting integer types */
object FormatCppWriter {

  /** Write an FPP format field as a C++ format string */
  def writeField(f: Format.Field, tn: AstNode[Ast.TypeName]): String = {
    import Format.Field._
    def getDecimalFormat(name: Ast.TypeInt) = (TypeUtils.signedness(name), TypeUtils.width(name)) match {
      case (TypeUtils.Unsigned, w) => s"%\" PRIu${w.toString} \""
      case (TypeUtils.Signed, w) => s"%\" PRIi${w.toString} \""
    }
    def default = tn.data match {
      case Ast.TypeNameFloat(_) => "%f"
      case Ast.TypeNameInt(name) => getDecimalFormat(name)
      case Ast.TypeNameQualIdent(_) => "%s"
      case Ast.TypeNameBool => "%d" // C++ boolean is promoted to int in printf
      case Ast.TypeNameString(_) => "%s"
    }
    def integer(t: Integer.Type) = tn.data match {
      case Ast.TypeNameInt(name) => t match {
        case Integer.Character => "%c"
        case Integer.Decimal => getDecimalFormat(name)
        case Integer.Hexadecimal => s"%\" PRIx${TypeUtils.width(name).toString} \""
        case Integer.Octal => s"%\" PRIo${TypeUtils.width(name).toString} \""
      }
      case _ => default
    }
    def rational(precision: Option[Int], t: Rational.Type) = tn.data match {
      case Ast.TypeNameFloat(_) =>
        val precisionStr = precision match {
          case Some(p) => s".${p.toString}"
          case None => ""
        }
        t match {
          case Rational.Exponent => s"%${precisionStr}e"
          case Rational.Fixed => s"%${precisionStr}f"
          case Rational.General => s"%${precisionStr}g"
        }
      case _ => default
    }

    f match {
      case Default => default
      case Integer(t) => integer(t)
      case Rational(precision, t) => rational(precision, t)
    }
  }

  def write(f: Format, tn: AstNode[Ast.TypeName]): String = {
    def escapePercent(s: String) = s.replaceAll("%", "%%")
    f.fields.foldLeft(escapePercent(f.prefix))((a, s) =>
      a + (s match {
        case (field, suffix) => writeField(field, tn) + escapePercent(suffix)
      })
    )
  }
}