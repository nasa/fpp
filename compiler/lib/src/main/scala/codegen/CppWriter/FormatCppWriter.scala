package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write an FPP format as C++ */
object FormatCppWriter {
  def writeField(f: Format.Field, tn: AstNode[Ast.TypeName]): String = {
    import Format.Field._
    def default = tn.data match {
      case Ast.TypeNameFloat(_) => "%f"
      case Ast.TypeNameInt(name) => name match {
        case Ast.I8()
           | Ast.I16()
           | Ast.I32()
           | Ast.I64() => "%d"
        case Ast.U8()
           | Ast.U16()
           | Ast.U32()
           | Ast.U64() => "%u"
      }
      case Ast.TypeNameQualIdent(_) => "%s"
      case Ast.TypeNameBool => "%d"
      case Ast.TypeNameString(_) => "%s"
    }
    def integer(t: Integer.Type) = tn.data match {
      case Ast.TypeNameInt(name) => t match {
        case Integer.Character => "%c"
        case Integer.Decimal => name match {
          case Ast.I8()
             | Ast.I16()
             | Ast.I32()
             | Ast.I64() => "%d"
          case Ast.U8()
             | Ast.U16()
             | Ast.U32()
             | Ast.U64() => "%u"
        }
        case Integer.Hexadecimal => "%x"
        case Integer.Octal => "%o"
      }
      case _ => default
    }
    def rational(precision: Option[Int], t: Rational.Type) = tn.data match {
      case Ast.TypeNameFloat(name) => {
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

  def write(f: Format, tn: AstNode[Ast.TypeName]): String = {
    f.fields.foldLeft(f.prefix)((a, s) =>
      a + (s match {
        case (field, suffix) => writeField(field, tn) + suffix
      })
    )
  }
}