@ Top-level annotation, line 1
@ Top-level annotation, line 2
module A {
  struct StructOK1 {
    @ Member annotation, line 1.
    @ Member annotation, line 2.
    mF32: F32
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
}

module B {
  struct StructOK2 {
    s1: A.StructOK1
  }
}

array Struct1 = [2] A.StructOK1
array Struct2 = [2] B.StructOK2