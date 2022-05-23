package fpp.compiler.test

import fpp.compiler.ast._
import fpp.compiler.analysis._

import Helpers._
import Type._

/** Helper functions and values for types */
object Types {

  def absType(name: Ast.Ident, id: AstNode.Id = 0): AbsType = {
    val d = Ast.DefAbsType(name)
    val anode = annotatedNode(d, id)
    AbsType(anode)
  }

  def array(name: Ast.Ident, anonArray: AnonArray = AnonArray(None, U32), id: AstNode.Id = 0): Array = {
    val size = AstNode.create(Ast.ExprLiteralInt("1"))
    val eltType = AstNode.create(Ast.TypeNameInt(Ast.U32()))
    val d = Ast.DefArray(name, size, eltType, None, None)
    val anode = annotatedNode(d, id)
    Array(anode, anonArray)
  }

  def enumeration(name: Ast.Ident, repType: Type.PrimitiveInt = I32, id: AstNode.Id = 0): Enum = {
    val d = Ast.DefEnum(name, None, List(), None)
    val anode = annotatedNode(d, id)
    Enum(anode, repType)
  }

  def struct(name: Ast.Ident, anonStruct: AnonStruct = AnonStruct(Map()), id: AstNode.Id = 0): Struct = {
    val d = Ast.DefStruct(name, List(), None)
    val anode = annotatedNode(d, id)
    Struct(anode, anonStruct)
  }

  val defaultAbsType: AbsType = absType("T", 0)
  val defaultArray: Array = array("A", AnonArray(None, I32), 1)
  val defaultEnum: Enum = enumeration("E", I32, 2)
  val defaultStruct: Struct = struct("S", AnonStruct(Map()), 3)

}
