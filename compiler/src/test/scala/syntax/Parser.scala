package fpp.compiler.test

import fpp.compiler.syntax.{Lexer,Parser,Token}
import java.io.File
import java.io.FileReader
import org.scalatest.wordspec.AnyWordSpec

class ParserSpec extends AnyWordSpec {

  "type name OK" should {
    def parse(s: String): Unit = {
      Parser.parseString(Parser.typeName, s) match {
        case Right(_) => ()
        case Left(_) => assert(false)
      }
    }
    val typeNames = List(
      "I8",
      "I16",
      "I32",
      "I64",
      "U8",
      "U16",
      "U32",
      "U64",
      "F32",
      "F64",
      "bool",
      "string",
      "a",
      "a.b",
      "a.b.c",
    )
    typeNames.foreach { tn => s"parse $tn" in parse(tn) }
  }

  "type name error" should {
    def parse(s: String): Unit = {
      Parser.parseString(Parser.typeName, s) match {
        case Right(r) => assert(false)
        case Left(_) => ()
      }
    }
    val typeNames = List(
      "@",
      "a.",
      "1+3",
    )
    typeNames.foreach { tn => s"not parse $tn" in parse(tn) }
  }

}
