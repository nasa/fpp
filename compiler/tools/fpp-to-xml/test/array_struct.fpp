module A {
  struct ComprehensiveStruct {
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

struct Struct2D {
  s1: A.ComprehensiveStruct
}

array Struct1 = [2] A.ComprehensiveStruct

module B {
  array Struct2 = [3] Struct2D
}
