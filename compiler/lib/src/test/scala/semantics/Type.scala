package fpp.compiler.test

import fpp.compiler.ast._
import fpp.compiler.analysis._
import org.scalatest.wordspec.AnyWordSpec

import Type._

class TypeSpec extends AnyWordSpec {

  "type identity" should {
    val identicalPairs = List(
      duplicate(absType("T")),
      duplicate(array("A")),
      duplicate(enum("E")),
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
    val distinctPairs = List(
      (absType("T0", 0), absType("T1", 1)),
      (enum("E0", I32, 0), enum("E1", U32, 1)),
      (array("A0", AnonArray(None, I32), 0), array("A1", AnonArray(None, U32), 1)),
      (struct("S0", AnonStruct(Map()), 0), struct("S1", AnonStruct(Map()), 1)),
      (array("A", AnonArray(None, I32), 0), struct("S0", AnonStruct(Map()), 1)),
      (Boolean,String),
      (F32,F64),
      (I8,U32),
      duplicate(AnonArray(None, I32)),
      duplicate(AnonStruct(Map())),
    )
    identicalPairs.foreach { pair => s"hold for $pair" in assert(areIdentical(pair._1, pair._2)) }
    distinctPairs.foreach { pair => s"not hold for $pair" in assert(!areIdentical(pair._1, pair._2)) }
  }

  "type conversion" should {
    val allowedPairs = List(
      duplicate(I32),
      I32 -> F32,
      enum("E") -> I32,
      duplicate(AnonArray(None, I32)),
      duplicate(AnonArray(Some(3), I32)),
      AnonArray(Some(3), I32) -> AnonArray(Some(3), AnonArray(None, U32)),
      AnonArray(None, I32) -> AnonArray(Some(3), I32),
      AnonArray(Some(3), I32) -> AnonArray(None, I32),
      AnonArray(None, enum("E")) -> AnonArray(None, I32),
      array("A", AnonArray(None, I32)) -> AnonArray(None, U32),
      array("A0", AnonArray(None, I32), 0) -> array("A1", AnonArray(None, U32), 1),
      String -> AnonArray(None, String),
      enum("E") -> AnonArray(None, I32),
      AnonArray(Some(3), I32) -> AnonArray(Some(3), AnonArray(Some(3), I32)),
      duplicate(AnonStruct(Map("x" -> I32))),
      AnonStruct(Map("x" -> I32)) -> AnonStruct(Map("x" -> AnonArray(None, I32))),
      AnonStruct(Map("x" -> I32)) -> AnonStruct(Map("x" -> I32) + ("y" -> I32)),
      struct("S", AnonStruct(Map("x" -> I32))) -> AnonStruct(Map("x" -> I32)),
      struct("S0", AnonStruct(Map("x" -> I32)), 0) -> struct("S1", AnonStruct(Map("x" -> I32)), 1),
      String -> AnonStruct(Map("x" -> String)),
      AnonStruct(Map("x" -> I32)) -> AnonStruct(Map("x" -> AnonStruct(Map("y" -> I32)))),
    )
    val disallowedPairs = List(
      String -> Boolean,
      I32 -> enum("E"),
      AnonArray(None, I32) -> AnonArray(None, enum("E")),
      AnonArray(Some(3), I32) -> AnonArray(Some(4), I32),
      array("A", AnonArray(None, I32)) -> AnonArray(None, String),
      array("A0", AnonArray(None, I32), 0) -> array("A1", AnonArray(None, String), 1),
      String -> AnonArray(None, I32),
      AnonStruct(Map("x" -> I32)) -> AnonStruct(Map("x" -> String)),
      AnonStruct(Map("x" -> I32)) -> AnonStruct(Map("y" -> I32)),
      struct("S", AnonStruct(Map("x" -> I32))) -> AnonStruct(Map("x" -> String)),
      struct("S0", AnonStruct(Map("x" -> I32)), 0) -> struct("S1", AnonStruct(Map("x" -> String)), 1),
      String -> AnonStruct(Map("x" -> I32)),
      AnonArray(None, I32) -> AnonStruct(Map()),
      AnonStruct(Map()) -> AnonArray(None, I32),
    )
    allowedPairs.foreach { pair => s"allow ${pair._1} -> ${pair._2}" in assert(mayBeConverted(pair)) }
    disallowedPairs.foreach { pair => s"not allow ${pair._1} -> ${pair._2}" in assert(!mayBeConverted(pair)) }
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

  def enum(name: Ast.Ident, repType: Type = I32, id: AstNode.Id = 0) = {
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
