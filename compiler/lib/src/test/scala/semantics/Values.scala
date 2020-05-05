package fpp.compiler.test

import fpp.compiler.analysis._

import Helpers._
import Types._
import Value._

object Values {

  val defaultI8 = PrimitiveInt(0, Type.PrimitiveInt.I8)
  val defaultI16 = PrimitiveInt(0, Type.PrimitiveInt.I16)
  val defaultI32 = PrimitiveInt(0, Type.PrimitiveInt.I32)
  val defaultI64 = PrimitiveInt(0, Type.PrimitiveInt.I64)
  val defaultU8 = PrimitiveInt(0, Type.PrimitiveInt.U8)
  val defaultU16 = PrimitiveInt(0, Type.PrimitiveInt.U16)
  val defaultU32 = PrimitiveInt(0, Type.PrimitiveInt.U32)
  val defaultU64 = PrimitiveInt(0, Type.PrimitiveInt.U64)

  val defaultInteger = Integer(0)

  val defaultF32 = Float(0, Type.Float.F32)
  val defaultF64 = Float(0, Type.Float.F64)

  val defaultBoolean = Boolean(false)

  val defaultString = String("")

  val defaultAnonArray3U32 = AnonArray(List.fill(3)(defaultU32))
  val defaultAnonArray3I32 = AnonArray(List.fill(3)(defaultI32))

}
