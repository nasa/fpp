type T

module S {
  @ A struct with a member array
  struct ArrayStructMemberArray {
    mU32Array: [3] U32
    mF64Scalar: F64
  }
}

module A {
  array ArrayStructMemberArray = [5] S.ArrayStructMemberArray
}
