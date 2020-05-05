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
      (defaultString, Type.String),
      (defaultAnonArray3U32, defaultAnonArray3U32Type),
      (array, arrayType),
      (enum, enumType),
      (anonStruct, anonStructType),
      (struct, structType),
    )
    pairs.foreach { 
      pair => s"evaluate ${pair._1} to ${pair._2}" in 
      assert(pair._1.getType == pair._2) 
    }
  }

}
