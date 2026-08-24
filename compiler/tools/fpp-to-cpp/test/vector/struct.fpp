module M {
  struct S1 {
    mU32: U32
    mF64: F64
    mString: string
  }
}

@ A vector of structs
vector Struct1 = [size 3] M.S1
