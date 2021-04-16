@ Top-level annotation, line 1
@ Top-level annotation, line 2
struct StructOK1 {
  @ Member annotation, line 1.
  @ Member annotation, line 2.
  mF32: [3] F32
  mF64: F64
  mI16: I16
  mI32: I32
  mI64: I64
  mI8: I8
  mU16: U16
  mU32: U32
  mU64: U64
  mU8: U8
  m_bool: bool
  m_string: string
}

struct StructOK2 {
  s1: StructOK1
}
