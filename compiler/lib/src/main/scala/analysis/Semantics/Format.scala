package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._
import scala.util.parsing.combinator.RegexParsers

/** An FPP presentation format */
case class Format(
  /** The first part of the format, before any fields */
  prefix: String,
  /** The list of pairs of fields followed by suffix strings */
  fields: List[(Format.Field,String)]
)

object Format {

  sealed trait Field

  object Field {

    case object Default extends Field

    case class Integer(t: Integer.Type) extends Field

    object Integer {
      sealed trait Type
      case object Binary extends Type
      case object Character extends Type
      case object Decimal extends Type
      case object Hexadecimal extends Type
      case object Octal extends Type
    }

    case class Floating(precision: Option[Int], t: Floating.Type) extends Field

    object Floating {
      sealed trait Type
      case object Exponent extends Type
      case object Fixed extends Type
      case object General extends Type
      case object Percent extends Type
    }
  }

  /** Parse a format string into a format */
  object Parser extends RegexParsers {

    override def skipWhitespace = false

    def string: Parser[String] = "([^{}]|{{|}})*".r

    def precision: Parser[Int] = "." ~>! "[0-9]+".r  ^^ { _.toInt }

    def field: Parser[Field] = {
      def integer = {
        def binary = "b" ^^ { case _ => Field.Integer(Field.Integer.Binary) }
        def character = "c" ^^ { case _ => Field.Integer(Field.Integer.Character) }
        def decimal = "d" ^^ { case _ => Field.Integer(Field.Integer.Decimal) }
        def hexadecimal = "x" ^^ { case _ => Field.Integer(Field.Integer.Hexadecimal) }
        binary | character | decimal | hexadecimal
      }
      def floating = {
        def ty = {
          def exponent = "E|e".r ^^ { case _ => Field.Floating.Exponent }
          def fixed = "F|f".r ^^ { case _ => Field.Floating.Fixed }
          def general = "G|g".r ^^ { case _ => Field.Floating.General }
          def percent = "%" ^^ { case _ => Field.Floating.Percent }
          exponent | fixed | general | percent
        }
        opt(precision) ~ ty ^^ { case p ~ t => Field.Floating(p, t) }
      }
      "{" ~>! (integer | floating) <~! "}"
    }

  }

}
