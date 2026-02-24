@ Definitions and specifiers
module DefinitionsAndSpecifiers {

  @ Abstract type definition
  type T
  @< Abstract type definition

  @ Type alias definition
  type TA = T
  @< Type alias definition

  @ Array definition
  array A = [10] U32 default 0 format "{} counts"
  @< Array definition

  @ State machine outside a component
  state machine SO
  @< State machine outside a component

  @ Interface definition
  interface I {
    @ General port instance specifier
    sync input port pI: [10] P priority 10 assert
    @< General port instance specifier

    @ Special port instance specifier
    command recv port cmdIn
    @< Special port instance specifier

    @ Interface import specifier
    import J
    @< Interface import specifier
  }
  @< Interface definition

  @ Component definition
  active component C {

    type T
    array A = [3] U32
    struct S { x: [3] U32, y: F32, z: string }
    enum E { X, Y, Z } default X

    @ Container specifier
    product container C id 0x00 default priority 10
    @< Container specifier

    @ Record specifier
    product record R: U32 array id 0x00
    @< Record specifier

    @ Command specifier
    async command C(a: U32, b: F32) opcode 0x00 priority 10 assert
    @< Command specifier

    @ Parameter specifier
    external param P: U32 default 0 id 0x00 set opcode 0x01 save opcode 0x02
    @< Parameter specifier

    @ General port instance specifier
    sync input port p1: [10] P priority 10 assert
    @< General port instance specifier

    @ Special port instance specifier
    command recv port cmdIn
    @< Special port instance specifier

    @ Async product receive port
    async product recv port productRecvIn priority 10 assert
    @< Async product receive port

    @ Internal Component State machine definition
    state machine S
    @< State machine definition

    @ State machine instance 1
    state machine instance s1: S priority 10 drop
    @< State machine instance 1

    @ State machine instance 2
    state machine instance s2: S
    @< State machine instance 2

    @ State machine for outside definition
    state machine instance so: SO
    @< State machine for outside definition

    output port p2: [10] P
    @ Port matching specifier
    match p1 with p2
    @< Port matching specifier

    @ Telemetry channel specifier
    telemetry T: U32 id 0x00 update on change format "{} s" \
      low { red 0, orange 1, yellow 2 } \
      high { yellow 10, orange 11, red 12 }
    @< Telemetry channel specifier

    @ Event specifier
    event E(a: U32, b: F32) severity activity low id 0x00 format "{} counts" throttle 10
    @< Event specifier

    @ Event specifier with throttle timeout
    event ET(a: U32, b: F32) severity activity high id 0x00 format "{} counts" throttle 10 every {seconds=10}
    @< Event specifier

    @ Internal port specifier
    internal port I(a: U32, b: F32) priority 10 assert
    @< Internal port specifier

    @ Interface import specifier
    import I
    @< Interface import specifier
  }
  @< Component definition

  @ Simple component instance definition
  instance c1: C1 base id 0x100
  @< Simple component instance definition

  @ Component instance definition
  instance c2: C2 base id 0x200 type "T" at "C2.hpp" queue size 100 stack size 1024 priority 10 cpu 0 {
    @ Init specifier
    phase CONSTRUCTION """
    line 1
      line 2
    line 3
    """
    @< Init specifier
  }
  @< Component instance definition

  @ Constant definition
  constant x = 0
  @< Constant definition

  @ Enum definition
  enum E : I32 {
    @ X
    X = 1
    @< X
    @ Y
    Y = 2
    @< Y
  }
  @< Enum definition

  @ Module definition
  module M {

    constant x = 0

  }
  @< Module definition

  @ Include specifier
  include "constant.fppi"
  @< Include specifier

  @ Port definition
  port P(a: U32, b: F32) -> U32
  @< Port definition

  @ Struct definition
  struct S {
    @ x
    x: U32 format "{} s"
    @< x
    @ y
    y: F32 format "{} m/s"
    @< y
  }
  @< Struct definition

  @ Topology definition
  topology T {

    @ Public instance specifier
    instance i1
    @< Public instance specifier

    @ Direct connection graph specifier
    connections C {
      i1.p[0] -> i2.p[1]
      unmatched i1.p1[0] -> i2.p2[0]
      unmatched i1.p1 -> i2.p2
    }
    @< Direct connection graph specifier

    @ Graph pattern specifier
    command connections instance i1 { i2, i3, i4 }
    @< Graph pattern specifier

    @ Topology instance specifier
    import T1
    @< Topology instance specifier

    @ Telemetry packet group
    telemetry packets P {

      @ Telemetry packet
      packet P1 id 0 group 0 {
        i1.c1
        i2.c2
      }
      @< Telemetry packet

      @ Include specifier
      include "packet.fppi"
      @< Include specifier

    } omit {
      i3.c3
    }
    @< Telemetry packet group

    @ Topology port specifier
    port a = b.a
    @< Topology port specifier
  }
  @< Topology definition

  @ Topology definition with one implements
  topology T implements I {}
  @< Topology definition with one implements

  @ Topology definition with two implements
  topology T implements I, I {}
  @< Topology definition with two implements

  @ Location specifier
  locate instance i at "instances.fpp"
  @< Location specifier

}
@< Definitions and specifiers

@ Type names
module TypeNames {

  array typeNameU32 = [10] U32
  array typeNameF32 = [10] F32
  array typeNameBool = [10] bool
  array typeNameString = [10] string size 256
  array typeNameQID = [10] a.b.c

}
@< Type names

@ Expressions
module Expressions {

  @ Arithmetic
  constant arithExp = 1 + 2 * 3 - -4 * 5 + 6
  @< Arithmetic

  @ Array
  constant arrayExp = [ 1, 2, 3 ]
  @< Array

  @ Array Subscript
  constant arraySubExp1 = [ 1, 2, 3 ][1]
  @< Array Subscript

  @ Array Subscript
  constant arraySubExp2 = a.b.c[1]
  @< Array Subscript

  @ Array Subscript with member selection
  constant arraySubExp2 = a.b.c[1][2].s[12]
  @< Array Subscript 

  @ Boolean literal
  constant booleanLiteralExp = true
  @< Boolean literal

  @ Dot
  constant dotExp = a.b.c
  @< Dot

  @ FP literal
  constant fpLiteralExp = 0.1234
  @< FP literal

  @ Identifier
  constant identExp = x
  @< Identifier

  @ Int literal
  constant intLiteralExp = 1234
  @< Int literal

  @ Paren
  constant parenExp = (1 + 2) * 3
  @< Paren

  @ String literal single
  constant stringLiteralSingleExp = "This is a string."
  @< String literal single

  @ String literal multi
  constant stringLiteralMultExp = """
                                  line 1
                                  line 2
                                  line 3
                                  """
  @< String literal multi

  @ Struct
  constant structExp = { a = 1, b = 2, c = 3 }
  @< Struct

}
@< Expressions
