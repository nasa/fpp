type T

module M1 {
  struct ArrayStructS3 {
    mU32Array: [3] U32
    mF64Scalar: F64
  }
}
@ An array of structs with array member to test scalar argument constructor 
array ArrayStruct3 = [5] M1.ArrayStructS3
