package fpp.compiler.test

import fpp.compiler.syntax.{Lexer,Parser,Token}
import java.io.File
import java.io.FileReader
import org.scalatest.wordspec.AnyWordSpec

class ParserSpec extends AnyWordSpec {

  "connection OK" should {
    parseAllOK(
      Parser.connection,
      List(
        "a -> b",
        "a.b -> c.d",
        "a.b.c -> d.e.f",
      )
    )
  }

  "spec connection graph direct OK" should {
    parseAllOK(
      Parser.specConnectionGraph,
      List(
        "connections C {}",
        "connections C { a-> b }",
        "connections C { a-> b, c -> d }",
      )
    )
  }

  "spec connection graph pattern OK" should {
    parseAllOK(
      Parser.specConnectionGraph,
      List(
        "connections instance a.b pattern P",
        "connections instance a.b {} pattern P",
        "connections instance a.b { c.d } pattern P"
      )
    )
  }

  "spec event OK" should {
    parseAllOK(
      Parser.specEvent,
      List(
        "event E severity activity high",
        "event E severity activity low",
        "event E severity command",
        "event E severity diagnostic",
        "event E severity fatal",
        "event E severity warning high",
        "event E severity warning low",
        "event E () severity activity high",
        "event E (x: U32) severity activity high",
        "event E severity activity high id 0x100",
        "event E (x: U32) severity activity high id 0x100 format \"x={}\"",
        "event E (x: U32) severity activity high id 0x100 format \"x={}\" throttle 10",
      )
    )
  }

  "spec include OK" should {
    parseAllOK(
      Parser.specInclude,
      List(
        "include \"file.fpp\"",
      )
    )
  }

  "spec init OK" should {
    parseAllOK(
      Parser.specInit,
      List(
        "init a.b phase 0 \"string\"",
      )
    )
  }

  "def abs type OK" should {
    parseAllOK(
      Parser.defAbsType,
      List(
        "type t",
      )
    )
  }

  "def array OK" should {
    parseAllOK(
      Parser.defArray,
      List(
        "array a = [10] U32",
        "array a = [10] U32 default 0",
        "array a = [10] U32 default 0 format \"{} counts\"",
      )
    )
  }

  "def component OK" should {
    parseAllOK(
      Parser.defComponent,
      List(
        "active component C { }",
        "passive component C { }",
        "queued component C { }",
      )
    )
  }

  "expression OK" should {
    parseAllOK(
      Parser.exprNode,
      List(
        "-1",
        "1+1",
        "1-1",
        "1*1",
        "1/1",
        "[0, 1, 2]",
        """[
             0
             1
             2
           ]""",
        """[
             0,
             1,
             2
           ]""",
        """[
             0,
             1,
             2,
           ]""",
        "true",
        "false",
        "a.b",
        "a.b.c",
        "1.0",
        "a",
        "1",
        "\"abc\"",
        "{ x = 1, y = 2, z = 3 }",
        """{
             x = 1
             y = 2
             z = 3
           }""",
        """{
             x = 1,
             y = 2,
             z = 3
           }""",
        """{
             x = 1,
             y = 2,
             z = 3,
           }"""
      )
    )
  }

  "expression error" should {
    parseAllError(
      Parser.exprNode,
      List(
        "@",
        "a.",
        "bool",
      )
    )
  }

  "formal param list OK" should {
    parseAllOK(
      Parser.formalParamList,
      List(
        "",
        "()",
        "(x: U32)",
        "(x: U32, y: F64)",
        """(
             x: U32
             y: F64
           )""",
        """(
             @ Pre
             x: U32 @< Post
             @ Pre
             y: F64 @< Post
           )""",
        """(
             x: U32,
             y: F64
           )""",
        """(
             @ Pre
             x: U32, @< Post
             @ Pre
             y: F64 @< Post
           )""",
        """(
             x: U32,
             y: F64,
           )""",
        """(
             @ Pre
             x: U32, @< Post
             @ Pre
             y: F64, @< Post
           )""",
        "(ref x: string)",
        "(ref x: string size 256)",
      )
    )
  }

  "spec command OK" should {
    parseAllOK(
      Parser.specCommand,
      List(
        "async command C",
        "guarded command C",
        "sync command C",
        "async command C()",
        "async command C(x: U32)",
        "async command C(x: U32) opcode 0x100",
        "async command C(x: U32) opcode 0x100 priority 10",
        "async command C assert",
        "async command C block",
        "async command C drop",
      )
    )
  }

  "spec component instance OK" should {
    parseAllOK(
      Parser.specCompInstance,
      List(
        "instance a",
        "instance a.b",
        "instance a.b.c",
        "private instance a",
        "private instance a.b",
        "private instance a.b.c",
      )
    )
  }

  "type name OK" should {
    parseAllOK(
      Parser.typeName,
      List(
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
    )
  }

  "type name error" should {
    parseAllError(
      Parser.typeName,
      List(
        "@",
        "a.",
        "1+3",
      )
    )
  }

  def parseAllError[T](p: Parser.Parser[T], ss: List[String]): Unit = {
    ss.foreach { s => s"not parse $s" in parseError(p, s) }
  }

  def parseAllOK[T](p: Parser.Parser[T], ss: List[String]): Unit = {
    ss.foreach { s => s"parse $s" in parseOK(p, s) }
  }

  def parseError[T](p: Parser.Parser[T], s: String): Unit = {
    Parser.parseString(p, s) match {
      case Right(r) => assert(false)
      case Left(_) => ()
    }
  }

  def parseOK[T](p: Parser.Parser[T], s: String): Unit = {
    Parser.parseString(p, s) match {
      case Right(_) => ()
      case Left(_) => assert(false)
    }
  }

}
