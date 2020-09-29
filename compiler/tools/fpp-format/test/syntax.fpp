@ Definitions and specifiers
module DefinitionsAndSpecifiers {

  @ Abstract type definition
  type T
  @< Abstract type definition

  @ Array definition
  array A = [10] U32 default 0 format "{} counts"
  @< Array definition

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
  include "constant.fpp"
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

  @ Location specifier
  locate constant x at "constant.fpp"
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
