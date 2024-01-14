@ Top-level annotation, line 1
@ Top-level annotation, line 2
struct Primitive {
  @ Member annotation, line 1.
  @ Member annotation, line 2.
  varF32: [3] F32
  varF64: F64
  varI16: I16
  varI32: I32
  varI64: I64
  varI8: I8
  varU16: U16
  varU32: U32
  varU64: U64
  varU8: U8
  varBool: bool
  varString: string
}

struct PrimitiveStruct {
  s1: Primitive
}
