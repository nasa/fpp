package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP presentation format */
case class Format(
  /** The first part of the format, before any fields */
  prefix: String,
  /** The list of pairs of fields followed by suffix strings */
  pairs: List[(Format.Field,String)]
)

object Format {

  sealed trait Field

  object Field {

    case object Default extends Field

    case class Int(t: Int.Type) extends Field

    object Int {
      sealed trait Type
      case object Binary extends Type
      case object Character extends Type
      case object Decimal extends Type
      case object Hexadecimal extends Type
      case object Octal extends Type
    }

    case class Float(precision: Int, t: Float.Type) extends Field

    object Float {
      sealed trait Type
      case object Exponent extends Type
      case object Fixed extends Type
      case object General extends Type
      case object Percent extends Type
    }
  }

}
