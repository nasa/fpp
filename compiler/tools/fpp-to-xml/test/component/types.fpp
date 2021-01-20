passive component Types {

  array A = [3] U32

  enum E { X }

  struct S { x: U32, y: F32 }

  type T

}

array Types_AUse = [3] Types.A
array Types_EUse = [3] Types.E
array Types_SUse = [3] Types.S
array Types_TUse = [3] Types.T
