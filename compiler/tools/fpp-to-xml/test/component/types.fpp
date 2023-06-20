module M {

  passive component Types {

    array A = [3] U32

    enum E { X }

    struct S { x: U32, y: F32 }

    type T

    array AUse = [3] Types.A
    array EUse = [3] Types.E
    array SUse = [3] Types.S
    array TUse = [3] Types.T

  }

}
