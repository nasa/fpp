package fpp.compiler.test

import fpp.compiler.syntax.{Lexer,Token}
import java.io.File
import java.io.FileReader
import org.scalatest.wordspec.AnyWordSpec

class LexerSpec extends AnyWordSpec {

  "Error" should {
    def error(file: File): Unit = {
      val reader = new FileReader(file)
      Lexer.parse(Lexer.tokens, reader) match {
        case Lexer.Success(_, _) => assert(false)
        case _ => ()
      }
    }
    val dir = new File("lib/src/test/input/syntax/lexer/error")
    val files = dir.listFiles.filter(_.isFile)
      .filter(_.getName.endsWith(".fpp"))
      .foreach { file => s"not lex $file" in error(file) }
  }

  "FP literal" should {
    def lex(s: String): Unit = {
      Lexer.parse(Lexer.tokens, s) match {
        case Lexer.Success(Token.LITERAL_FLOAT(s1) :: List(), _) => assert(s1 === s)
        case _ => assert(false)
      }
    }
    val literals = List(
      "123.456",
      "123.",
      "456.",
      "123e10",
      "123e-10",
    )
    literals.foreach { s => s"lex $s to an FP literal" in lex(s) }
  }

  "OK" should {
    def ok(file: File): Unit = {
      val reader = new FileReader(file)
      Lexer.parse(Lexer.tokens, reader) match {
        case Lexer.Success(_, _) => ()
        case Lexer.NoSuccess(msg, next) => {
          println(msg)
          println(next.pos.longString)
          assert(false)
        }
        // Suppress false compiler warning
        case _ => throw new InternalError("This cannot happen")
      }
    }
    val dir = new File("lib/src/test/input/syntax/lexer/ok")
    val files = dir.listFiles.filter(_.isFile)
      .filter(_.getName.endsWith(".fpp"))
      .foreach { file => s"lex $file" in ok(file) }
  }

  "annotations" should {
    "lex \"@ abc\" to a pre-annotation" in {
      Lexer.parse(Lexer.tokens, "@ abc") match {
        case Lexer.Success(Token.PRE_ANNOTATION("abc") :: List(), _) => ()
        case _ => assert(false)
      }
    }
    "lex \"@< abc\" to a post-annotation" in {
      Lexer.parse(Lexer.tokens, "@< abc") match {
        case Lexer.Success(Token.POST_ANNOTATION("abc") :: List(), _) => ()
        case _ => assert(false)
      }
    }
  }

  "int literal" should {
    def lex(s: String): Unit = {
      Lexer.parse(Lexer.tokens, s) match {
        case Lexer.Success(Token.LITERAL_INT(s1) :: List(), _) => assert(s1 === s)
        case _ => assert(false)
      }
    }
    val literals = List(
      "123456",
      "0x123456ABCD",
    )
    literals.foreach { s => s"lex $s to an int literal" in lex(s) }
  }

  "line continuation" should {
    "lex \"\\n\" to one EOL" in {
      val s = "\n"
      Lexer.parse(Lexer.tokens, s) match {
        case Lexer.Success(Token.EOL() :: List(), _) => ()
        case _ => assert(false)
      }
    }
    "lex \"\\\\n\" to zero tokens" in {
      val s = "\\\n"
      Lexer.parse(Lexer.tokens, s) match {
        case Lexer.Success(List(), _) => ()
        case _ => assert(false)
      }
    }
  }

  "string literal" should {
    def lex(s: String): Unit = {
      Lexer.parse(Lexer.tokens, s) match {
        case Lexer.Success(Token.LITERAL_STRING(_) :: List(), _) => ()
        case _ => assert(false)
      }
    }
    val literals = List(
      "\"abc\"",
      "\"\\\" abc\"",
      "\"abc \\\"\"",
      "\"abc \\\" abc\"",
      "\"\"\"abc\"\"\"",
      "\"\"\"\\\" abc\"\"\"",
      "\"\"\"abc \\\"\"\"\"",
      "\"\"\"abc \\\" abc\"\"\"",
      "\"\"\"abc \" abc\"\"\"",
      "\"\"\"abc \"\" abc\"\"\"",
      "\"\\\\q\"",
      "\"\"\"\\\\q\"\"\""
    )
    literals.foreach { s => s"lex $s to a string literal" in lex(s) }
  }
}
