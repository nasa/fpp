type T

module M1 {
  struct ArrayStructS1 {
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
    m_absType: T
  }
}

struct ArrayStructS2 {
  s1: M1.ArrayStructS1
}

array ArrayStruct1 = [2] M1.ArrayStructS1

module M2 {
  array ArrayStruct2 = [3] ArrayStructS2 @< Array with struct arg
}
