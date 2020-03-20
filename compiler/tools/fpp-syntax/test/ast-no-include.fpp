@ Definitions and specifiers
module DefinitionsAndSpecifiers {

  @ Abstract type definition
  type T
  @< Abstract type definition

  @ Array definition
  array A = [10] U32 default 0 format "{} counts"
  @< Array definition

  @ Component definition
  active component C {

    @ Command specifier
    async command C(x: U32) opcode 0x00 priority 10 assert
    @< Command specifier

    @ Parameter specifier
    param P: U32 default 0 id 0x00 set opcode 0x01 save opcode 0x02
    @< Parameter specifier
    
    @ General port instance specifier
    sync input port p: [10] P priority 10 assert
    @< General port instance specifier

    @ Special port instance specifier
    command recv port cmdIn
    @< Special port instance specifier

    @ Telemetry channel specifier
    telemetry T: U32 id 0x00 update on change format "{} s" \
      low { red 0, orange 1, yellow 2 } \
      high { yellow 10, orange 11, red 12 }
    @< Telemetry channel specifier

    @ Event specifier
    event E(x: U32) severity activity low id 0x00 format "{} counts" throttle 10
    @< Event specifier

    @ Internal port specifier
    internal port I(x: F32) priority 10 assert
    @< Internal port specifier

  }
  @< Component definition

  @ Component instance definition
  instance c: C base id 0x100 queue size 100 stack size 1024 priority 10
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
  include "file.fpp"
  @< Include specifier

  @ Init specifier
  init i phase CONSTRUCTION """
  line 1
    line 2
  line 3
  """

  @ Port definition
  port P(x: U32) -> U32
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

    @ Private instance specifier
    private instance i2
    @< Private instance specifier

    @ Direct connection graph specifier
    connections C { i1.p[0] -> i2.p[1] }
    @< Direct connection graph specifier

    @ Graph pattern specifier
    connections instance i1 { i2, i3, i4 } pattern COMMAND
    @< Graph pattern specifier

    @ Topology import specifier
    import T1
    @< Topology import specifier

    @ Unused port specifier
    unused { a.p, b.p, c.p }
    @< Unused port specifier

  }
  @< Topology definition

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
  array typeNameString = [10] string
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

  @ Parens
  constant parensExp = (1 + 2) * 3
  @< Parens

}
@< Expressions
