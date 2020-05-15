package fpp.compiler.test

import fpp.compiler.ast._
import fpp.compiler.analysis._
import org.scalatest.wordspec.AnyWordSpec

import Helpers._

class FormatSpec extends AnyWordSpec {

  def parse(s: String) = Format.Parser.parse(Format.Parser.parseAllInput(Format.Parser.format), s)

  "format" should {
    val ok = List(
      ("abcd", Format("abcd", Nil)),
      ("ab{{cd", Format("ab{cd", Nil)),
      ("ab}}cd", Format("ab}cd", Nil)),
      ("ab{}cd", Format("ab", List((Format.Field.Default, "cd")))),
      ("ab{b}cd", Format("ab", List((Format.Field.Integer(Format.Field.Integer.Binary), "cd")))),
      ("ab{c}cd", Format("ab", List((Format.Field.Integer(Format.Field.Integer.Character), "cd")))),
      ("ab{d}cd", Format("ab", List((Format.Field.Integer(Format.Field.Integer.Decimal), "cd")))),
      ("ab{x}cd", Format("ab", List((Format.Field.Integer(Format.Field.Integer.Hexadecimal), "cd")))),
      ("ab{e}cd", Format("ab", List((Format.Field.Floating(None, Format.Field.Floating.Exponent), "cd")))),
      ("ab{E}cd", Format("ab", List((Format.Field.Floating(None, Format.Field.Floating.Exponent), "cd")))),
      ("ab{f}cd", Format("ab", List((Format.Field.Floating(None, Format.Field.Floating.Fixed), "cd")))),
      ("ab{F}cd", Format("ab", List((Format.Field.Floating(None, Format.Field.Floating.Fixed), "cd")))),
      ("ab{g}cd", Format("ab", List((Format.Field.Floating(None, Format.Field.Floating.General), "cd")))),
      ("ab{G}cd", Format("ab", List((Format.Field.Floating(None, Format.Field.Floating.General), "cd")))),
      ("ab{%}cd", Format("ab", List((Format.Field.Floating(None, Format.Field.Floating.Percent), "cd")))),
      ("ab{.3e}cd", Format("ab", List((Format.Field.Floating(Some(3), Format.Field.Floating.Exponent), "cd")))),
      ("ab{.3E}cd", Format("ab", List((Format.Field.Floating(Some(3), Format.Field.Floating.Exponent), "cd")))),
      ("ab{.3f}cd", Format("ab", List((Format.Field.Floating(Some(3), Format.Field.Floating.Fixed), "cd")))),
      ("ab{.3F}cd", Format("ab", List((Format.Field.Floating(Some(3), Format.Field.Floating.Fixed), "cd")))),
      ("ab{.3g}cd", Format("ab", List((Format.Field.Floating(Some(3), Format.Field.Floating.General), "cd")))),
      ("ab{.3G}cd", Format("ab", List((Format.Field.Floating(Some(3), Format.Field.Floating.General), "cd")))),
      ("ab{.3%}cd", Format("ab", List((Format.Field.Floating(Some(3), Format.Field.Floating.Percent), "cd")))),
    )
    val error = List(
      "{",
      "}",
      "ab{1234xyz}cd",
      "ab{.3b}cd",
    )
    ok.foreach { 
      pair => s"parse ${pair._1} as ${pair._2}" in  {
        parse(pair._1) match {
          case Format.Parser.Success(result, _) => assert(result == pair._2)
          case _ => assert(false)
        }
      }
    }
    error.foreach {
      input => s"not parse ${input}" in {
        parse(input) match {
          case Format.Parser.NoSuccess(_, _) => ()
          case _ => assert(false)
        }
      }
    }
  }

}
