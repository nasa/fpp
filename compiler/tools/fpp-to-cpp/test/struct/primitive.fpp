@ Top-level annotation, line 1
@ Top-level annotation, line 2
struct Primitive {
  @ Member annotation, line 1.
  @ Member annotation, line 2.
  memberF32: [3] F32
  memberF64: F64
  memberI16: I16
  memberI32: I32
  memberI64: I64
  memberI8: I8
  memberU16: U16
  memberU32: U32
  memberU64: U64
  memberU8: U8
  memberBool: bool
  memberString: string
}

struct PrimitiveStruct {
  s1: Primitive
}
