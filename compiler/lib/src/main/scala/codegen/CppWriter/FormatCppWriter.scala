package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Write an FPP format as C++
 *  Uses PRI macros from cinttypes header for formatting integer types */
object FormatCppWriter {

  /** Get the PRI macro for a decimal integer type */
  def getDecimalFormat(pit: Type.PrimitiveInt): String =
    (pit.signedness, pit.bitWidth) match {
      case (Type.PrimitiveInt.Unsigned, w) => s"%\" PRIu${w.toString} \""
      case (Type.PrimitiveInt.Signed, w) => s"%\" PRIi${w.toString} \""
    }

  /** Write an FPP format field as a C++ format string */
  def writeField(s: CppWriterState, f: Format.Field, tn: AstNode[Ast.TypeName]): String = {
    import Format.Field._
    def default = s.a.typeMap(tn.id).getUnderlyingType match {
      case _: Type.Float => "%f"
      case pit: Type.PrimitiveInt => getDecimalFormat(pit)
      case Type.Boolean => "%d" // C++ Boolean is promoted to int in printf
      case _ => "%s"
    }
    def integer(it: Integer.Type) = s.a.typeMap(tn.id).getUnderlyingType match {
      case pit: Type.PrimitiveInt => it match {
        case Integer.Character => "%c"
        case Integer.Decimal => getDecimalFormat(pit)
        case Integer.Hexadecimal => s"%\" PRIx${pit.bitWidth.toString} \""
        case Integer.Octal => s"%\" PRIo${pit.bitWidth.toString} \""
      }
      case _ => default
    }
    def rational(precision: Option[BigInt], rt: Rational.Type) = tn.data match {
      case Ast.TypeNameFloat(_) =>
        val precisionStr = precision match {
          case Some(p) => s".${p.toString}"
          case None => ""
        }
        rt match {
          case Rational.Exponent => s"%${precisionStr}e"
          case Rational.Fixed => s"%${precisionStr}f"
          case Rational.General => s"%${precisionStr}g"
        }
      case _ => default
    }

    f match {
      case Default => default
      case Integer(it) => integer(it)
      case Rational(precision, rt) => rational(precision, rt)
    }
  }

  def write(s: CppWriterState, f: Format, tn: AstNode[Ast.TypeName]): String = {
    f.fields.foldLeft (escapePercent(f.prefix)) {
      case (a, (field, suffix)) =>
        a + writeField(s, field, tn) + escapePercent(suffix)
    }
  }

  def escapePercent(s: String) = s.replaceAll("%", "%%")
}
