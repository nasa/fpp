package fpp.compiler.test

import fpp.compiler.ast._
import fpp.compiler.analysis._
import org.scalatest.wordspec.AnyWordSpec

import Helpers._
import Value._
import Values._

class ValueSpec extends AnyWordSpec {

  "getType" should {
    val pairs = List(
      (defaultI8, Type.I8),
      (defaultI16, Type.I16),
      (defaultI32, Type.I32),
      (defaultI64, Type.I64),
      (defaultU8, Type.U8),
      (defaultU16, Type.U16),
      (defaultU32, Type.U32),
      (defaultU64, Type.U64),
      (defaultInteger, Type.Integer),
      (defaultF32, Type.F32),
      (defaultF64, Type.F64),
      (defaultBoolean, Type.Boolean),
      (defaultString, Type.String(None)),
      (defaultAnonArray3U32, defaultAnonArray3U32Type),
      (array, arrayType),
      (enumeration, enumType),
      (anonStruct, anonStructType),
      (struct, structType),
    )
    pairs.foreach { 
      pair => s"evaluate ${pair._1} to ${pair._2}" in 
      assert(pair._1.getType == pair._2) 
    }
  }

  "add" should {
    val triples = List(
      (createI8(1), createI8(2), Some(createI8(3))),
      (createI8(1), Integer(2), Some(Integer(3))),
      (createI8(1), createF32(2), Some(createF64(3))),
      (createF32(1), createF32(2), Some(createF32(3))),
      (createF32(1), createF64(2), Some(createF64(3))),
      (enumeration, createI32(1), Some(createI32(1))),
      (enumeration, createI16(1), Some(Integer(1))),
      (createI32(1), String("abc"), None),
      (createI32(1), defaultAnonArray3U32, None),
      (createI32(1), array, None),
      (createI32(1), anonStruct, None),
      (createI32(1), struct, None),
    )
    triples.foreach { 
      triple => s"sum ${triple._1} and ${triple._2} to ${triple._3}" in 
      assert(triple._1 + triple._2 == triple._3) 
    }
  }

  "is zero" should {
    val pairs = List(
      (createI32(1), false),
      (createI32(0), true),
      (enumeration, true),
      (createF32(1), false),
      (createF32(0), true),
      (array, false),
      (defaultAnonArray3U32, false),
      (struct, false),
      (anonStruct, false),
    )
    pairs.foreach { 
      pair => s"evaluate ${pair._1} to ${pair._2}" in 
      assert(pair._1.isZero == pair._2) 
    }
  }

  "convert to type" should {
    val triples = List(
      (createI8(1), Type.I8, Some(createI8(1))),
      (createI8(1), Type.I16, Some(createI16(1))),
      (createI8(1), Type.F32, Some(createF32(1))),
      (Integer(1), Type.I8, Some(createI8(1))),
      (createI8(1), Type.Integer, Some(Integer(1))),
      (createF64(1), Type.Integer, Some(Integer(1))),
      (createF64(1), Type.F32, Some(createF32(1))),
      (enumeration, Type.F32, Some(createF32(0))),
      (createI32(0), enumType, None),
      (createI32(42), defaultAnonArray3U32Type, Some(createAnonArray(3, createU32(42)))),
      (anonStruct, defaultAnonArray3U32Type, None),
      (array, defaultAnonArray3U32Type, Some(defaultAnonArray3U32)),
      (anonStruct, arrayType, None),
      (createAnonArray(2, createI8(1)), defaultAnonArray3U32Type, None),
      (anonStruct, anonStructType, Some(anonStruct)),
      (anonStruct, Type.AnonStruct(Map("a" -> Type.String(None))), None),
      (
        anonStruct,
        Type.AnonStruct(Map("a" -> Type.U32, "b" -> Type.String(None), "c" -> Type.I8)),
        Some(AnonStruct(anonStruct.members + ("c" -> defaultI8)))
      ),
      (
        struct,
        Type.AnonStruct(Map("a" -> Type.U32, "b" -> Type.String(None), "c" -> Type.I8)),
        Some(AnonStruct(anonStruct.members + ("c" -> defaultI8)))
      ),
      (createI32(0), anonStructType, None),
      (
        createI32(0),
        Type.AnonStruct(Map("a" -> Type.U32)),
        Some(AnonStruct(Map("a" -> defaultU32)))
      ),
    )
    triples.foreach { 
      triple => s"evaluate ${triple._1} and ${triple._2} to ${triple._3}" in 
      assert(triple._1.convertToType(triple._2) == triple._3) 
    }
  }

  "negate" should {
    val pairs = List(
      (createI8(1), Some(createI8(-1))),
      (createF32(1), Some(createF32(-1))),
      (Integer(1), Some(Integer(-1))),
      (defaultString, None),
      (defaultAnonArray3U32, None),
      (anonStruct, None),
      (EnumConstant(("X", 1), Types.defaultEnum), Some(createI32(-1))),
    )
    pairs.foreach { 
      pair => s"evaluate ${pair._1} to ${pair._2}" in 
      assert(- pair._1 == pair._2) 
    }
  }

  "truncate" should {
    val pairs = List(
      (createU8(256), createU8(0)),
      (createI8(256), createI8(0)),
      (createU8(257), createU8(1)),
      (createI8(257), createI8(1)),
      (createU8(-1), createU8(255)),
      (createAnonArray(3, createU8(256)), createAnonArray(3, createU8(0))),
      (createAnonArray(3, createI8(256)), createAnonArray(3, createI8(0))),
      (createAnonArray(3, createU8(257)), createAnonArray(3, createU8(1))),
      (createAnonArray(3, createI8(257)), createAnonArray(3, createI8(1))),
      (createAnonArray(3, createU8(-1)), createAnonArray(3, createU8(255))),
    )
    pairs.foreach { 
      pair => s"evaluate ${pair._1} to ${pair._2}" in 
      assert(pair._1.truncate == pair._2) 
    }
  }

}
