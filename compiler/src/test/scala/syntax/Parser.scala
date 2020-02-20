package fpp.compiler.test

import fpp.compiler.syntax.{Lexer,Parser,Token}
import java.io.File
import java.io.FileReader
import org.scalatest.wordspec.AnyWordSpec

class ParserSpec extends AnyWordSpec {

  "type name" should {
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

}
