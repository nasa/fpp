module M {
  struct S1 {
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
    mBool: bool
    mString: string
  }
}

struct S2 {
  s1: M.S1
}

module S {
  @ A struct with a member array
  struct S3 {
    mU32Array: [3] U32
    mF64: F64
  }
}

@ Struct with a default value
struct SDefault { x: U32 } default { x = 42 }

@ Struct wrapping a struct with a default field
struct SWrapper { s: SDefault }

@ An array of structs
array Struct1 = [5] M.S1

array Struct2 = [3] S2 @< Array of structs with struct member

@ An array of structs with array member
array Struct3 = [3] S.S3

@ An array of structs wrapping a struct with a default member
array Struct4 = [3] SWrapper
