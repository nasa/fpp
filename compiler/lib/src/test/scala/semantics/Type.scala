package fpp.compiler.test

import fpp.compiler.ast._
import fpp.compiler.analysis._
import org.scalatest.wordspec.AnyWordSpec

import Type._

class TypeSpec extends AnyWordSpec {

  "identical types" should {
    val pairs = List(
      duplicate(absType("T")),
      duplicate(array("A")),
      duplicate(enum("E", I32)),
      duplicate(struct("S")),
      duplicate(Boolean),
      duplicate(F32),
      duplicate(F64),
      duplicate(I16),
      duplicate(I32),
      duplicate(I64),
      duplicate(I8),
      duplicate(Integer),
      duplicate(String),
      duplicate(U16),
      duplicate(U32),
      duplicate(U64),
      duplicate(U8),
    )
    pairs.foreach { pair => s"be identical: $pair" in assert(areIdentical(pair._1, pair._2)) }
  }

  "distinct types" should {
    val pairs = List(
      (absType("T0", 0), absType("T1", 1)),
      (enum("E0", I32, 0), enum("E1", U32, 1)),
      (array("A0", AnonArray(None, I32), 0), array("A1", AnonArray(None, U32), 1)),
      (struct("S0", AnonStruct(Map()), 0), struct("S1", AnonStruct(Map()), 1)),
      (array("A", AnonArray(None, I32), 0), struct("S0", AnonStruct(Map()), 1)),
      (Boolean,String),
      (F32,F64),
      (I8,U32),
    )
    pairs.foreach { pair => s"be distinct: $pair" in assert(!areIdentical(pair._1, pair._2)) }
  }

  def absType(name: Ast.Ident, id: AstNode.Id = 0) = {
    val d = Ast.DefAbsType(name)
    val anode = annotatedNode(d, id)
    AbsType(anode)
  }

  def annotate[T](x: T) = (List(), x, List())

  def annotatedNode[T](x: T, id: AstNode.Id) = annotate(AstNode.create(x, id))

  def array(name: Ast.Ident, anonArray: AnonArray = AnonArray(None, U32), id: AstNode.Id = 0) = {
    val size = AstNode.create(Ast.ExprLiteralInt("1"))
    val eltType = AstNode.create(Ast.TypeNameInt(Ast.U32()))
    val d = Ast.DefArray(name, size, eltType, None, None)
    val anode = annotatedNode(d, id)
    Array(anode, anonArray)
  }

  def duplicate[T](x: T) = (x, x)

  def enum(name: Ast.Ident, repType: Type, id: AstNode.Id = 0) = {
    val d = Ast.DefEnum(name, None, List())
    val anode = annotatedNode(d, id)
    Enum(anode, repType)
  }

  def struct(name: Ast.Ident, anonStruct: AnonStruct = AnonStruct(Map()), id: AstNode.Id = 0) = {
    val d = Ast.DefStruct(name, List(), None)
    val anode = annotatedNode(d, id)
    Struct(anode, anonStruct)
  }

}
