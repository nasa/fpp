package fpp.compiler.test

import fpp.compiler.ast._
import fpp.compiler.analysis._
import org.scalatest.wordspec.AnyWordSpec

import Helpers._
import Type._
import Types._

class TypeSpec extends AnyWordSpec {

  "type identity" should {
    val identicalPairs = List(
      duplicate(defaultAbsType),
      duplicate(defaultArray),
      duplicate(defaultEnum),
      duplicate(defaultStruct),
      duplicate(defaultAliasType),
      duplicate(Boolean),
      duplicate(F32),
      duplicate(F64),
      duplicate(I16),
      duplicate(I32),
      duplicate(I64),
      duplicate(I8),
      duplicate(Integer),
      duplicate(U16),
      duplicate(U32),
      duplicate(U64),
      duplicate(U8),
    )
    val distinctPairs = List(
      (absType("T0", 0), absType("T1", 1)),
      (enumeration("E0", I32, 0), enumeration("E1", U32, 1)),
      (array("A0", AnonArray(None, I32), 0), array("A1", AnonArray(None, U32), 1)),
      (struct("S0", AnonStruct(Map()), 0), struct("S1", AnonStruct(Map()), 1)),
      (array("A", AnonArray(None, I32), 0), struct("S0", AnonStruct(Map()), 1)),
      (defaultAbsType, defaultEnum),
      (defaultArray, defaultStruct),
      (Boolean,String(None)),
      duplicate(String(None)),
      (F32,F64),
      (I8,U32),
      duplicate(AnonArray(None, I32)),
      duplicate(AnonStruct(Map())),
      (aliasType("TAliasU32", U32, 0), U32),
      (aliasType("AliasE", enumeration("E", I32, 0), 1), enumeration("E", I32, 0))
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
      enumeration("E") -> I32,
      duplicate(AnonArray(None, I32)),
      duplicate(AnonArray(Some(3), I32)),
      AnonArray(Some(3), I32) -> AnonArray(Some(3), AnonArray(None, U32)),
      AnonArray(None, I32) -> AnonArray(Some(3), I32),
      AnonArray(Some(3), I32) -> AnonArray(None, I32),
      AnonArray(None, enumeration("E")) -> AnonArray(None, I32),
      array("A", AnonArray(None, I32)) -> AnonArray(None, U32),
      array("A0", AnonArray(None, I32), 0) -> array("A1", AnonArray(None, U32), 1),
      String(None) -> AnonArray(None, String(None)),
      enumeration("E") -> AnonArray(None, I32),
      AnonArray(Some(3), I32) -> AnonArray(Some(3), AnonArray(Some(3), I32)),
      duplicate(AnonStruct(Map("x" -> I32))),
      AnonStruct(Map("x" -> I32)) -> AnonStruct(Map("x" -> AnonArray(None, I32))),
      AnonStruct(Map("x" -> I32)) -> AnonStruct(Map("x" -> I32, "y" -> I32)),
      struct("S", AnonStruct(Map("x" -> I32))) -> AnonStruct(Map("x" -> I32)),
      struct("S0", AnonStruct(Map("x" -> I32)), 0) -> struct("S1", AnonStruct(Map("x" -> I32)), 1),
      String(None) -> AnonStruct(Map("x" -> String(None))),
      AnonStruct(Map("x" -> I32)) -> AnonStruct(Map("x" -> AnonStruct(Map("y" -> I32)))),
      duplicate(defaultAliasType),
      enumeration("E", I32, 0) -> aliasType("AliasE", enumeration("E", I32, 0), 1),
      aliasType("AliasE", enumeration("E", I32, 0), 1) -> enumeration("E", I32, 0),
      aliasType("AliasE", enumeration("E", I32, 0), 1) -> I32,
      defaultAliasType -> defaultAbsType,
      aliasType("AliasU32", U32, 0) -> U32,
      U32 -> aliasType("AliasU32", U32, 0),
    )
    val disallowedPairs = List(
      String(None) -> Boolean,
      I32 -> enumeration("E"),
      AnonArray(None, I32) -> AnonArray(None, enumeration("E")),
      AnonArray(Some(3), I32) -> AnonArray(Some(4), I32),
      array("A", AnonArray(None, I32)) -> AnonArray(None, String(None)),
      array("A0", AnonArray(None, I32), 0) -> array("A1", AnonArray(None, String(None)), 1),
      String(None) -> AnonArray(None, I32),
      AnonStruct(Map("x" -> I32)) -> AnonStruct(Map("x" -> String(None))),
      AnonStruct(Map("x" -> I32)) -> AnonStruct(Map("y" -> I32)),
      struct("S", AnonStruct(Map("x" -> I32))) -> AnonStruct(Map("x" -> String(None))),
      struct("S0", AnonStruct(Map("x" -> I32)), 0) -> struct("S1", AnonStruct(Map("x" -> String(None))), 1),
      String(None) -> AnonStruct(Map("x" -> I32)),
      AnonArray(None, I32) -> AnonStruct(Map()),
      AnonStruct(Map()) -> AnonArray(None, I32),
      I32 -> aliasType("AliasE", enumeration("E")),
      struct("S", AnonStruct(Map("x" -> I32))) -> aliasType("U32Alias", enumeration("E", U16, 1), 2),
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
      (enumeration("E", I32), I32) -> I32,
      (enumeration("E", I32), F64) -> F64,
      (I32, enumeration("E", I32)) -> I32,
      (F64, enumeration("E", I32)) -> F64,
      (enumeration("E0", I32, 0), enumeration("E1", I32, 1)) -> I32,
      (enumeration("E0", I32, 0), enumeration("E1", I8, 1)) -> Integer,
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
      // (enum E, type E1 = E) -> E
      (enumeration("E", I32), aliasType("E1", enumeration("E"), 1)) -> enumeration("E"),
      // Simple common underlying type
      (aliasType("E1", enumeration("E"), 1), aliasType("E2", enumeration("E"), 2)) -> enumeration("E"),
      // Common ancestor #1
      (
        aliasType("E3", aliasType("E1", enumeration("E"), 2), 4),
        aliasType("E2", aliasType("E1", enumeration("E"), 2), 5),
      ) -> aliasType("E1", enumeration("E"), 2),
      // Common ancestor #2
      (
        aliasType("E2", aliasType("E1", enumeration("E"), 2), 5),
        aliasType("E4", aliasType("E3", aliasType("E1", enumeration("E"), 2), 4), 6),
      ) -> aliasType("E1", enumeration("E"), 2),
      // No common ancestor
      // Compute using the underlying type
      (
        aliasType("A1", enumeration("E0", I32, 0), 3),
        aliasType("A2", enumeration("E1", I32, 1), 4)
      ) -> I32
    )
    val disallowedPairs = List(
      (String(None), Boolean),
      (AnonArray(None, I32), AnonStruct(Map())),
      (AnonStruct(Map()), AnonArray(None, I32)),
      (AnonArray(Some(3), I32), AnonArray(Some(4), I8)),
      (AnonArray(Some(3), I32), AnonArray(Some(3), String(None))),
      (String(None), AnonArray(Some(3), Integer)),
      (AnonArray(Some(3), Integer), String(None)),
      (AnonArray(Some(4), Integer), AnonArray(Some(3), AnonArray(Some(4), Integer))),
      (AnonStruct(Map("x" -> Integer)), AnonStruct(Map("x" -> String(None)))),
      (String(None), AnonStruct(Map("x" -> Integer))),
      (AnonStruct(Map("x" -> Integer)), String(None)),
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

  "displayable type" should {
    val displayable = List(
      I32,
      F32,
      Boolean,
      String(None),
      defaultEnum,
      defaultArray,
      defaultStruct,
      array("A1", AnonArray(None, array("A2", AnonArray(None, I32)))),
      struct("S1", AnonStruct(Map("x" -> I32)))
    )
    val notDisplayable = List(
      Integer,
      AnonArray(None, I32),
      AnonStruct(Map()),
      defaultAbsType,
      array("A3", AnonArray(None, defaultAbsType)),
      array("A4", AnonArray(None, array("A5", AnonArray(None, defaultAbsType)))),
      struct("S2", AnonStruct(Map("x" -> defaultAbsType))),
      struct(
        "S3",
        AnonStruct(
          Map("x" -> struct("S4", AnonStruct(Map("x" -> defaultAbsType))))
        )
      )
    )
    displayable.foreach {
      ty => s"be true for $ty" in
      assert(ty.isDisplayable)
    }
    notDisplayable.foreach {
      ty => s"be false for $ty" in
      assert(!ty.isDisplayable)
    }
  }

  "size of" should {
    // Types and constants used in test
    val enum1 = enumeration("E", U16, 0)
    val enumAlias = aliasType("EnumAlias", enum1, 1)
    val array1 = array("A", AnonArray(Some(3), I64), 2)
    val array2 = array("A2", AnonArray(Some(2), array1), 3)
    val stringAlias = aliasType("StringAlias", stringWithSize("100", 4))
    val stringSize10 = stringWithSize("10", 5)
    val fwSizeStoreType = aliasType("FwSizeStoreType", U16, 6)
    val fwFixedLengthStringSize = annotate(
      AstNode.create(
        Ast.DefConstant(
          "FW_FIXED_LENGTH_STRING_SIZE",
          AstNode.create(Ast.ExprLiteralInt("256"), 7),
          false
        ), 8
      )
    )
    // Create analysis data structure and fill in the framework definitions, types, and values 
    // required for computing the serialized size of a type
    val a = Analysis(
      frameworkDefinitions = FrameworkDefinitions(
        constantMap = Map(
          "FW_FIXED_LENGTH_STRING_SIZE" -> Symbol.Constant(fwFixedLengthStringSize)
        ),
        typeMap = Map(
          "FwSizeStoreType" -> Symbol.AliasType(fwSizeStoreType.node)
        )
      ),
      typeMap = Map(6 -> fwSizeStoreType),
      valueMap = Map(
        8 -> Value.Integer(256),
        4 -> Value.Integer(100),
        5 -> Value.Integer(10)
      )
    )

    "compute the size of primitives" in {
        assert(SerializedSize.ty(a, U8) == Some(1))
        assert(SerializedSize.ty(a, U16) == Some(2))
        assert(SerializedSize.ty(a, U32) == Some(4))
        assert(SerializedSize.ty(a, U64) == Some(8))
        assert(SerializedSize.ty(a, I8) == Some(1))
        assert(SerializedSize.ty(a, I16) == Some(2))
        assert(SerializedSize.ty(a, I32) == Some(4))
        assert(SerializedSize.ty(a, I64) == Some(8))
        assert(SerializedSize.ty(a, F32) == Some(4))
        assert(SerializedSize.ty(a, F64) == Some(8))
        assert(SerializedSize.ty(a, Boolean) == Some(1))
      }

      "compute the size of alias types" in {
        assert(SerializedSize.ty(a, stringAlias) == Some(102))
        assert(
          SerializedSize.ty(
            a,
            aliasType("StringDefaultAlias", String(None))
          ) == Some(258)
        )
        assert(SerializedSize.ty(a, enumAlias) == Some(2))
      }

      "compute the size of arrays" in {
        assert(SerializedSize.ty(a, array1) == Some(24))
        assert(SerializedSize.ty(a, array2) == Some(48))
      }

      "compute the size of enums" in {
        assert(SerializedSize.ty(a, enum1) == Some(2))
      }

      "compute the size of structs" in {
        val struct1 = struct(
          "S",
          AnonStruct(
            Map(
              "m1" -> array1,
              "m2" -> F64,
              "m3" -> enumAlias,
              "m4" -> stringSize10
            )
          ),
          0,
          Map("m1" -> 2, "m3" -> 1, "m4" -> 3)
        )
        val struct2 = struct(
          "S2",
          AnonStruct(
            Map(
              "m1" -> array2,
              "m2" -> stringAlias,
              "m3" -> struct1
            )
          ),
          0,
          Map("m3" -> 2)
        )
        assert(SerializedSize.ty(a, struct1) == Some(94))
        assert(SerializedSize.ty(a, struct2) == Some(338))
      }

      "compute the size of types with no size" in {
        assert(SerializedSize.ty(a, defaultAbsType) == None)
        val A = array("A", AnonArray(Some(3), defaultAbsType))
        assert(SerializedSize.ty(a, A) == None)
        val S = struct(
          "S",
          AnonStruct(Map("m1" -> I32, "m2" -> defaultAbsType))
        )
        assert(SerializedSize.ty(a, S) == None)
        val X = aliasType("X", defaultAbsType)
        assert(SerializedSize.ty(a, X) == None)
      }

    }

}
