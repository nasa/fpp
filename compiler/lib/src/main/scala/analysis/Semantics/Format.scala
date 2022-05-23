package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.Positional

/** An FPP presentation format */
case class Format(
  /** The first part of the format, before any fields */
  prefix: String,
  /** The list of pairs of fields followed by suffix strings */
  fields: List[(Format.Field,String)]
)

object Format {

  sealed trait Field extends Positional {
    def isInteger = false
    def isRational = false
    final def isNumeric: Boolean = isInteger || isRational
  }

  object Field {

    case object Default extends Field

    case class Integer(t: Integer.Type) extends Field {
      override def isInteger = true
    }

    object Integer {
      sealed trait Type
      case object Character extends Type
      case object Decimal extends Type
      case object Hexadecimal extends Type
      case object Octal extends Type
    }

    case class Rational(precision: Option[Int], t: Rational.Type) extends Field {
      override def isRational = true
    }

    object Rational {
      sealed trait Type
      case object Exponent extends Type
      case object Fixed extends Type
      case object General extends Type
    }
  }

  /** Parse a format string into a format */
  object Parser extends RegexParsers {

    override def skipWhitespace = false

    def string: Parser[String] = "([^\\{\\}]|\\{\\{|\\}\\})*".r ^^ {
      case s => {
        val s1 = "\\{\\{".r.replaceAllIn(s, "{")
        val s2 = "\\}\\}".r.replaceAllIn(s1, "}")
        s2
      }
    }

    def precision: Parser[Int] = "." ~>! "[0-9]+".r  ^^ { _.toInt }

    def field: Parser[Field] = positioned {
      def default = "{}" ^^ { case _ => Field.Default }
      def integer = {
        def character = "c" ^^ { case _ => Field.Integer(Field.Integer.Character) }
        def decimal = "d" ^^ { case _ => Field.Integer(Field.Integer.Decimal) }
        def hexadecimal = "x" ^^ { case _ => Field.Integer(Field.Integer.Hexadecimal) }
        def octal = "o" ^^ { case _ => Field.Integer(Field.Integer.Octal) }
        character | decimal | hexadecimal | octal
      }
      def rational = {
        def ty = {
          def exponent = "e".r ^^ { case _ => Field.Rational.Exponent }
          def fixed = "f".r ^^ { case _ => Field.Rational.Fixed }
          def general = "g".r ^^ { case _ => Field.Rational.General }
          exponent | fixed | general | failure("e, f, or g expected")
        }
        opt(precision) ~ ty ^^ { case p ~ t => Field.Rational(p, t) }
      }
      default | ("{" ~>! (integer | rational | failure("invalid replacement field")) <~! "}")
    }

    def format: Parser[Format] = string ~ rep(field ~ string) ^^ {
      case prefix ~ fields => Format(prefix, fields.map({ case field ~ string => (field, string) }))
    }

    def parseAllInput[T](p: Parser[T]): parseAllInput[T] = new parseAllInput[T](p)
    class parseAllInput[T](p: Parser[T]) extends Parser[T] {
      def dropWhile(in: Input, p: Char => Boolean): Input = {
        if (in.atEnd) in
        else if (p(in.first)) dropWhile(in.rest, p)
        else in
      }
      def apply(in: Input) = p(in) match {
        case s @ Success(out, in1) =>
          if (in1.atEnd) s
          else Failure("illegal character", dropWhile(in1, _ == ' '))
        case other => other
      }
    }
    
    def parseNode(node: AstNode[String]): Result.Result[Format] = {
      val loc = Locations.get(node.id)
      val string = node.data
      parse(parseAllInput(format), string) match {
        case NoSuccess(msg, next) => {
          val msg1 = "\n" ++ next.pos.longString ++ "\n" ++ msg
          Left(SemanticError.InvalidFormatString(loc, msg1))
        }
        case Success(result, _) => Right(result)
        // Suppress false compiler warning
        case _ => throw new InternalError("This cannot happen")
      }
    }

  }

}
