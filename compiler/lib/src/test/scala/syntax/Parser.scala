package fpp.compiler.test

import fpp.compiler.ast._
import fpp.compiler.syntax.{Lexer,Parser,Token}
import java.io.File
import java.io.FileReader
import org.scalatest.wordspec.AnyWordSpec

class ParserSpec extends AnyWordSpec {

  "annotation OK" should {
    val r = Parser.parseString(Parser.transUnit)(
      """

      @ Line 1

      @ Line 2

      active component C {} @< Line 3

                            @< Line 4

      """
    )
    val Right(Ast.TransUnit(List(tum))) = r
    val Ast.TUMember((pre, _, post)) = tum
    assert(pre == List("Line 1", "Line 2"))
    assert(post == List("Line 3", "Line 4"))
    ()
  }

  "comment OK" should {
    parseAllOK(
      Parser.transUnit,
      List(
        "# This is a comment",
      )
    )
  }

  "connection OK" should {
    parseAllOK(
      Parser.connection,
      List(
        "a.b -> c.d",
        "a.b.c -> d.e.f",
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
        """active component C {
          constant a = 0
          struct S { x: U32 }
          async command C
          param p: U32
          async input port p: P
          telemetry T: U32
          array A = [10] U32
          enum E { X = 0, Y = 1 }
          event E severity activity low format "Event E"
          include "a.fpp"
          internal port P
          state machine S
          state machine instance s1: S
          state machine instance s2: S
        }""",
        """active component C {
          @ Pre
          constant a = 0 @< Post
          @ Pre
          constant b = 0 @< Post
        }""",
      )
    )
  }

  "def component instance OK" should {
    parseAllOK(
      Parser.defComponentInstance,
      List(
        "instance i: C base id 0x100",
        "instance i: C base id 0x100 type \"T\"",
        "instance i: C base id 0x100 type \"T\" at \"file.hpp\"",
        "instance i: C base id 0x100 type \"T\" at \"file.hpp\" queue size 10",
        "instance i: C base id 0x100 type \"T\" at \"file.hpp\" queue size 10 stack size 1024",
        "instance i: C base id 0x100 type \"T\" at \"file.hpp\" queue size 10 stack size 1024 priority 3",
        "instance i: C base id 0x100 type \"T\" at \"file.hpp\" queue size 10 stack size 1024 priority 3 cpu 0",
        "instance i: C base id 0x100 type \"T\" at \"file.hpp\" queue size 10 stack size 1024 priority 3 cpu 0 { phase 0 \"code\" }",
      )
    )
  }

  "def constant OK" should {
    parseAllOK(
      Parser.defConstant,
      List(
        "constant a = 0",
      )
    )
  }

  "def enum OK" should {
    parseAllOK(
      Parser.defEnum,
      List(
        "enum E { X, Y }",
        "enum E { X = 0, Y = 1 }",
        """enum E {
          @ Pre
          X = 0 @< Post
          @ Pre
          Y = 1 @< Post
        }""",
      )
    )
  }

  "def module OK" should {
    parseAllOK(
      Parser.defModule,
      List(
        "module M {}",
        """module M {
          active component C {}
          instance i: C base id 0x100
          constant a = 0
          module M {}
          port P
          struct S {}
          topology T {}
          locate component C at "c.fpp"
          type T
          array A = [10] U32
          enum E { X, Y }
          include "a.fpp"
        }""",
        """module M {
          @ Pre
          active component C {} @< Post
        }""",
      )
    )
  }

  "def port OK" should {
    parseAllOK(
      Parser.defPort,
      List(
        "port P",
        "port P()",
        "port P(x: U32)",
        "port P(x: U32) -> U32",
      )
    )
  }

  "def state machine OK" should {
    parseAllOK(
      Parser.defStateMachine,
      List(
        "state machine S",
      )
    )
  }

  "def struct OK" should {
    parseAllOK(
      Parser.defStruct,
      List(
        "struct S {}",
        "struct S { x: U32, y: F32 }",
        "struct S { x: U32 format \"{} steps\", y: F32 format \"{} m/s\" }",
        "struct S { x: U32, y: F32 } default { x = 1, y = 2 }",
        "struct S { x: [3] U32 }",
        """struct S {
          @ Pre
          x: U32 @< Post
          @ Pre
          y: F32 @< Post
        }""",
      )
    )
  }

  "def topology OK" should {
    parseAllOK(
      Parser.defTopology,
      List(
        "topology T {}",
        """topology T {
          instance i
          connections C {}
          import T
          include "a.fpp"
        }""",
        """topology T {
          @ Pre
          instance i @< Post
        }""",
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

  "spec connection graph direct OK" should {
    parseAllOK(
      Parser.specConnectionGraph,
      List(
        "connections C {}",
        "connections C { a.b -> c.d }",
        "connections C { a.b -> c.d, e.f -> g.h }",
      )
    )
  }

  "spec connection graph pattern OK" should {
    parseAllOK(
      Parser.specConnectionGraph,
      List(
        "command connections instance a.b",
        "command connections instance a.b {}",
        "command connections instance a.b { c.d }"
      )
    )
  }

  "spec container OK" should {
    parseAllOK(
      Parser.specContainer,
      List(
        "product container C",
        "product container C id 0x100",
        "product container C id 0x100 default priority 10"
      )
    )
  }

  "spec event OK" should {
    parseAllOK(
      Parser.specEvent,
      List(
        "event E severity activity high format \"event E\"",
        "event E severity activity low format \"event E\"",
        "event E severity command format \"event E\"",
        "event E severity diagnostic format \"event E\"",
        "event E severity fatal format \"event E\"",
        "event E severity warning high format \"event E\"",
        "event E severity warning low format \"event E\"",
        "event E () severity activity high format \"event E\"",
        "event E (x: U32) severity activity high format \"x={}\"",
        "event E severity activity high id 0x100 format \"event E\"",
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

  "spec internal port OK" should {
    parseAllOK(
      Parser.specInternalPort,
      List(
        "internal port P",
        "internal port P()",
        "internal port P(x: U32)",
        "internal port P(x: U32) priority 10",
        "internal port P(x: U32) priority 10 assert",
      )
    )
  }

  "spec location OK" should {
    parseAllOK(
      Parser.specLoc,
      List(
        "locate component a.b at \"c.fpp\"",
        "locate instance a.b at \"c.fpp\"",
        "locate constant a.b at \"c.fpp\"",
        "locate port a.b at \"c.fpp\"",
        "locate topology a.b at \"c.fpp\"",
        "locate type a.b at \"c.fpp\"",
      )
    )
  }

  "spec param OK" should {
    parseAllOK(
      Parser.specParam,
      List(
        "param P: U32",
        "param P: U32 default 0",
        "param P: U32 default 0 id 0x100",
        "param P: U32 default 0 id 0x100 set opcode 0x00",
        "param P: U32 default 0 id 0x100 set opcode 0x00 save opcode 0x01",
      )
    )
  }

  "spec port instance general OK" should {
    parseAllOK(
      Parser.specPortInstance,
      List(
        "async input port p: a",
        "async input port p: a.b",
        "async input port p: serial",
        "guarded input port p: a",
        "output port p: T",
        "sync input port p: T",
        "async input port p: [10] T",
        "async input port p: [10] T priority 10",
        "async input port p: [10] T priority 10 assert",
      )
    )
  }

  "spec port instance special OK" should {
    parseAllOK(
      Parser.specPortInstance,
      List(
        "async product recv port p",
        "command recv port p",
        "command reg port p",
        "command resp port p",
        "event port p",
        "guarded product recv port p",
        "param get port p",
        "param set port p",
        "product get port p",
        "product recv port p",
        "product request port p",
        "product send port p",
        "sync product recv port p",
        "telemetry port p",
        "text event port p",
        "time get port p",
      )
    )
  }

  "spec port matching OK" should {
    parseAllOK(
      Parser.specPortMatching,
      List(
        "match p1 with p2",
      )
    )
  }

  "spec record OK" should {
    parseAllOK(
      Parser.specRecord,
      List(
        "product record R: U32",
        "product record R: U32 array",
        "product record R: U32 id 0x100"
      )
    )
  }

  "spec state machine instance OK" should {
    parseAllOK(
      Parser.specStateMachineInstance,
      List(
        "state machine instance s: S",
      )
    )
  }

  "spec tlm channel OK" should {
    parseAllOK(
      Parser.specTlmChannel,
      List(
        "telemetry C: U32",
        "telemetry C: U32 id 0x00",
        "telemetry C: U32 id 0x00 update always",
        "telemetry C: U32 id 0x00 update on change",
        """telemetry C: U32 id 0x00 \
             update on change \
             format "{} counts"""",
        """telemetry C: U32 id 0x00 \
             update on change \
             format "{} counts" \
             low {}""",
        """telemetry C: U32 id 0x00 \
             update on change \
             format "{} counts" \
             low { red 0 }""",
        """telemetry C: U32 id 0x00 \
             update on change \
             format "{} counts" \
             low { red 0, orange 0 }""",
        """telemetry C: U32 id 0x00 \
             update on change \
             format "{} counts" \
             low { red 0, orange 0, yellow 0 }""",
        """telemetry C: U32 id 0x00 \
             update on change \
             format "{} counts" \
             low {} \
             high {}""",
        """telemetry C: U32 id 0x00 \
             update on change \
             format "{} counts" \
             low { red 0 } \
             high { red 0 }""",
        """telemetry C: U32 id 0x00 \
             update on change \
             format "{} counts" \
             low { red 0, orange 0 } \
             high { red 0, orange 0 }""",
        """telemetry C: U32 id 0x00 \
             update on change \
             format "{} counts" \
             low { red 0, orange 0, yellow 0 } \
             high { red 0, orange 0, yellow 0 }""",
      )
    )
  }

  "spec top import OK" should {
    parseAllOK(
      Parser.specTopImport,
      List(
        "import a",
        "import a.b",
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

  "trans unit OK" should {
    parseAllOK(
      Parser.transUnit,
      List(
        "",
        """
        # Some newlines
        
        
        """,
        """
        active component C {}
        instance i: C base id 0x100
        constant a = 0
        module M {}
        port P
        struct S {}
        topology T {}
        locate component C at "c.fpp"
        type T
        array A = [10] U32
        enum E { X, Y }
        include "a.fpp"
        """,
        """
        @ Pre
        active component C {} @< Post
        """,
        """


        constant a = 0


        constant b = 1


        """,
      )
    )
  }

  "trans unit error" should {
    parseAllError(
      Parser.transUnit,
      List(
        "$",
        "abcd",
        "1+1",
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
    Parser.parseString(p)(s) match {
      case Right(r) => {
        Console.err.println(s"parsed $r")
        assert(false)
      }
      case Left(_) => ()
    }
  }

  def parseOK[T](p: Parser.Parser[T], s: String): Unit = {
    Parser.parseString(p)(s) match {
      case Right(_) => ()
      case Left(l) => {
        Console.err.println(s"failed with error $l")
        assert(false)
      }
    }
  }

}
