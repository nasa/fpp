package fpp.compiler.test

import fpp.compiler.analysis._

import Helpers._
import Types._
import Value._

object Values {

  def createI8(v: BigInt) = PrimitiveInt(v, Type.PrimitiveInt.I8)
  def createI16(v: BigInt) = PrimitiveInt(v, Type.PrimitiveInt.I16)
  def createI32(v: BigInt) = PrimitiveInt(v, Type.PrimitiveInt.I32)
  def createI64(v: BigInt) = PrimitiveInt(v, Type.PrimitiveInt.I64)
  def createU8(v: BigInt) = PrimitiveInt(v, Type.PrimitiveInt.U8)
  def createU16(v: BigInt) = PrimitiveInt(v, Type.PrimitiveInt.U16)
  def createU32(v: BigInt) = PrimitiveInt(v, Type.PrimitiveInt.U32)
  def createU64(v: BigInt) = PrimitiveInt(v, Type.PrimitiveInt.U64)

  val defaultI8 = createI8(0)
  val defaultI16 = createI16(0)
  val defaultI32 = createI32(0)
  val defaultI64 = createI64(0)
  val defaultU8 = createU8(0)
  val defaultU16 = createU16(0)
  val defaultU32 = createU32(0)
  val defaultU64 = createU64(0)

  val defaultInteger = Integer(0)

  def createF32(v: Double) = Float(v, Type.Float.F32)
  def createF64(v: Double) = Float(v, Type.Float.F64)

  val defaultF32 = createF32(0)
  val defaultF64 = createF64(0)

  val defaultBoolean = Boolean(false)

  val defaultString = String("")

  def createAnonArray(size: Int, v: Value) = AnonArray(List.fill(size)(v))
  val defaultAnonArray3U32 = createAnonArray(3, defaultU32)
  val defaultAnonArray3U32Type = Type.AnonArray(Some(3), Type.U32)
  val defaultAnonArray3I32 = AnonArray(List.fill(3)(defaultI32))
  val array = Array(defaultAnonArray3I32, Types.defaultArray)
  val arrayType = Types.defaultArray

  val enum = EnumConstant(0, Types.defaultEnum)
  val enumType = Types.defaultEnum

  val anonStruct = AnonStruct(Map("a" -> defaultU32, "b" -> defaultString))
  val anonStructType = Type.AnonStruct(Map("a" -> Type.U32, "b" -> Type.String(None)))
  val structType = Types.struct("S", anonStructType, 3)
  val struct = Struct(anonStruct, structType)

}
