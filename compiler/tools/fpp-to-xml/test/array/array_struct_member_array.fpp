type T

module S {
  @ A struct with a member array
  struct ArrayStructMemberArray {
    mU32Array: [3] U32
    mF64: F64
  }
}

module A {
  array ArrayStructMemberArray = [3] S.ArrayStructMemberArray
}
