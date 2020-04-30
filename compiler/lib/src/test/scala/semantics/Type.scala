package fpp.compiler.test

import fpp.compiler.ast._
import fpp.compiler.analysis._
import org.scalatest.wordspec.AnyWordSpec

import Type._

class TypeSpec extends AnyWordSpec {

  "type identity" should {
    val identicalPairs = List(
      duplicate(defaultAbsType),
      duplicate(defaultArray),
      duplicate(defaultEnum),
      duplicate(defaultStruct),
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
      (defaultAbsType, defaultEnum),
      (defaultArray, defaultStruct),
      (Boolean,String),
      (F32,F64),
      (I8,U32),
      duplicate(AnonArray(None, I32)),
      duplicate(AnonStruct(Map())),
    )
    identicalPairs.foreach { 
      pair => s"hold for $pair" in 
      assert(areIdentical(pair._1, pair._2)) 
    }
    distinctPairs.foreach { 
      pair => s"not hold for $pair" in
      assert(!areIdentical(pair._1, pair._2)) 
    }
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
      AnonStruct(Map("x" -> I32)) -> AnonStruct(Map("x" -> I32, "y" -> I32)),
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
    allowedPairs.foreach { 
      pair => s"allow ${pair._1} -> ${pair._2}" in
      assert(mayBeConverted(pair)) 
    }
    disallowedPairs.foreach { 
      pair => s"not allow ${pair._1} -> ${pair._2}" in 
      assert(!mayBeConverted(pair)) 
    }
  }

  "common types" should {
    def commonTypeOfPair(pair: (Type, Type)) = commonType(pair._1, pair._2)
    val allowedPairs = List(
      duplicate(I32) -> I32,
      duplicate(defaultAbsType) -> defaultAbsType,
      duplicate(defaultArray) -> defaultArray,
      duplicate(defaultEnum) -> defaultEnum,
      duplicate(defaultStruct) -> defaultStruct,
      (I32, I8) -> Integer,
      (F32, F64) -> F64,
      (I32, F64) -> F64,
      (F64, I32) -> F64,
      // Enums
      (enum("E", I32), I32) -> I32,
      (enum("E", I32), F64) -> F64,
      (I32, enum("E", I32)) -> I32,
      (F64, enum("E", I32)) -> F64,
      (enum("E0", I32, 0), enum("E1", I32, 1)) -> I32,
      (enum("E0", I32, 0), enum("E1", I8, 1)) -> Integer,
      // Arrays
      (AnonArray(Some(3), I32), AnonArray(Some(3), I8)) -> AnonArray(Some(3), Integer),
      (Integer, AnonArray(Some(3), Integer)) -> AnonArray(Some(3), Integer),
      (AnonArray(Some(3), Integer), Integer) -> AnonArray(Some(3), Integer),
      (AnonArray(Some(3), Integer), AnonArray(Some(3), AnonArray(Some(4), Integer))) -> 
        AnonArray(Some(3), AnonArray(Some(4), Integer)),
      (array("A0", AnonArray(Some(3), I32), 0), array("A1", AnonArray(Some(3), I8), 1)) -> 
        AnonArray(Some(3), Integer),
      (AnonArray(Some(3), I32), array("A1", AnonArray(Some(3), I8), 1)) -> 
        AnonArray(Some(3), Integer),
      (array("A0", AnonArray(Some(3), I32), 0), AnonArray(Some(3), I8)) -> 
        AnonArray(Some(3), Integer),
      // Structs
      (AnonStruct(Map("x" -> I32)), AnonStruct(Map("x" -> I32))) -> 
        AnonStruct(Map("x" -> I32)),
      (AnonStruct(Map("x" -> I32)), AnonStruct(Map("x" -> I8))) -> 
        AnonStruct(Map("x" -> Integer)),
      (AnonStruct(Map("x" -> I32)), AnonStruct(Map("y" -> I8))) -> 
        AnonStruct(Map("x" -> I32, "y" -> I8)),
      (AnonStruct(Map("x" -> I32)), AnonStruct(Map("x" -> I8, "y" -> I8))) -> 
        AnonStruct(Map("x" -> Integer, "y" -> I8)),
      (Integer, AnonStruct(Map("x" -> Integer))) -> AnonStruct(Map("x" -> Integer)),
      (AnonStruct(Map("x" -> Integer)), Integer) -> AnonStruct(Map("x" -> Integer)),
      (AnonStruct(Map("x" -> I32)), AnonStruct(Map("x" -> AnonStruct(Map("x" -> I8))))) -> 
        AnonStruct(Map("x" -> AnonStruct(Map("x" -> Integer)))),
      (struct("S0", AnonStruct(Map("x" -> I32)), 0), struct("A1", AnonStruct(Map("x" -> I8)), 1)) ->
        AnonStruct(Map("x" -> Integer)),
      (AnonStruct(Map("x" -> I32)), struct("A1", AnonStruct(Map("x" -> I8)), 1)) ->
        AnonStruct(Map("x" -> Integer)),
      (struct("S0", AnonStruct(Map("x" -> I32)), 0), AnonStruct(Map("x" -> I8))) ->
        AnonStruct(Map("x" -> Integer)),
    )
    val disallowedPairs = List(
      (String, Boolean),
      (AnonArray(None, I32), AnonStruct(Map())),
      (AnonStruct(Map()), AnonArray(None, I32)),
      (AnonArray(Some(3), I32), AnonArray(Some(4), I8)),
      (AnonArray(Some(3), I32), AnonArray(Some(3), String)),
      (String, AnonArray(Some(3), Integer)),
      (AnonArray(Some(3), Integer), String),
      (AnonArray(Some(4), Integer), AnonArray(Some(3), AnonArray(Some(4), Integer))),
      (AnonStruct(Map("x" -> Integer)), AnonStruct(Map("x" -> String))),
      (String, AnonStruct(Map("x" -> Integer))),
      (AnonStruct(Map("x" -> Integer)), String),
      (defaultArray, defaultStruct),
      (defaultStruct, defaultArray),
      (defaultArray, AnonStruct(Map())),
      (AnonStruct(Map()), defaultArray),
      (defaultStruct, AnonArray(None, I32)),
      (AnonArray(None,I32), defaultStruct),
    )
    allowedPairs.foreach { 
      pair => s"resolve ${pair._1} to ${pair._2}" in 
      assert(commonTypeOfPair(pair._1) == Some(pair._2)) 
    }
    disallowedPairs.foreach { 
      pair => s"not resolve ${pair}" in 
      assert(commonTypeOfPair(pair) == None) 
    }
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

  lazy val defaultAbsType = absType("T", 0)
  lazy val defaultArray = array("A", AnonArray(None, I32), 1)
  lazy val defaultEnum = enum("E", I32, 2)
  lazy val defaultStruct = struct("S", AnonStruct(Map()), 3)

}
