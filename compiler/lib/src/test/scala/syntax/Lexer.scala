package fpp.compiler.test

import fpp.compiler.syntax.{Lexer,Token,Context}
import fpp.compiler.util.{File => FppFile}
import java.io.File
import org.scalatest.wordspec.AnyWordSpec

class LexerSpec extends AnyWordSpec {

  "Error" should {
    def error(file: File): Unit = {
      val contents = scala.io.Source.fromFile(file)
      Lexer.Scanner(
        FppFile.Path(file.toPath),
        contents.toArray
      )(using Context()).list() match {
        case Left(_) => ()
        case Right(_) => assert(false)
      }
    }
    val dir = new File("lib/src/test/input/syntax/lexer/error")
    val files = dir.listFiles.filter(_.isFile)
      .filter(_.getName.endsWith(".fpp"))
      .foreach { file => s"not lex $file" in error(file) }
  }

  "FP literal" should {
    def lex(s: String): Unit = {
      Lexer.Scanner(
        FppFile.StdIn,
        s.toCharArray
      )(using Context()).list() match {
        case Right(Token.LITERAL_FLOAT(s1) :: List()) => assert(s1 === s)
        case Right(t) =>
          System.err.println(t)
          assert(false)
        case Left(e) =>
          e.print
          assert(false)
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
      val contents = scala.io.Source.fromFile(file)
      Lexer.Scanner(
        FppFile.Path(file.toPath),
        contents.toArray
      )(using Context()).list() match {
        case Right(_) => ()
        case Left(msg) => {
          msg.print
          assert(false)
        }
      }
    }
    val dir = new File("lib/src/test/input/syntax/lexer/ok")
    val files = dir.listFiles.filter(_.isFile)
      .filter(_.getName.endsWith(".fpp"))
      .foreach { file => s"lex $file" in ok(file) }
  }

  "annotations" should {
    "lex \"@ abc\" to a pre-annotation" in {
      Lexer.Scanner(
        FppFile.StdIn,
        "@ abc".toCharArray
      )(using Context()).list() match {
        case Right(Token.PRE_ANNOTATION("abc") :: List()) => ()
        case _ => assert(false)
      }
    }
    "lex \"@< abc\" to a post-annotation" in {
      Lexer.Scanner(
        FppFile.StdIn,
        "@< abc".toCharArray
      )(using Context()).list() match {
        case Right(Token.POST_ANNOTATION("abc") :: List()) => ()
        case _ => assert(false)
      }
    }
  }

  "int literal" should {
    def lex(s: String): Unit = {
      Lexer.Scanner(
        FppFile.StdIn,
        s.toCharArray
      )(using Context()).list() match {
        case Right(Token.LITERAL_INT(s1) :: List()) => assert(s1 === s)
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
      Lexer.Scanner(
        FppFile.StdIn,
        s.toCharArray
      )(using Context()).list() match {
        case Right(Token.EOL() :: List()) => ()
        case _ => assert(false)
      }
    }
    "lex \"\\\\n\" to zero tokens" in {
      val s = "\\\n"
      Lexer.Scanner(
        FppFile.StdIn,
        s.toCharArray
      )(using Context()).list() match {
        case Right(List()) => ()
        case _ => assert(false)
      }
    }
  }

  "string literal" should {
    def lex(s: String): Unit = {
      Lexer.Scanner(
        FppFile.StdIn,
        s.toCharArray
      )(using Context()).list() match {
        case Right(Token.LITERAL_STRING(_) :: List()) => ()
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
