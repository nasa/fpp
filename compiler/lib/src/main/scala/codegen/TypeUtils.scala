package fpp.compiler.codegen

import fpp.compiler.ast.Ast

/** Utilities for FPP types */
object TypeUtils {
  sealed trait Signedness
  case object Signed extends Signedness
  case object Unsigned extends Signedness

  /** Get the signedness of an integer type */
  def signedness(typeInt: Ast.TypeInt): Signedness = typeInt match {
    case Ast.I8() => Signed
    case Ast.I16() => Signed
    case Ast.I32() => Signed
    case Ast.I64() => Signed
    case Ast.U8() => Unsigned
    case Ast.U16() => Unsigned
    case Ast.U32() => Unsigned
    case Ast.U64() => Unsigned
  }

  /** Get the width of an integer type */
  def width(typeInt: Ast.TypeInt): Int = typeInt match {
    case Ast.I8() => 8
    case Ast.I16() => 16
    case Ast.I32() => 32
    case Ast.I64() => 64
    case Ast.U8() => 8
    case Ast.U16() => 16
    case Ast.U32() => 32
    case Ast.U64() => 64
  }
}
