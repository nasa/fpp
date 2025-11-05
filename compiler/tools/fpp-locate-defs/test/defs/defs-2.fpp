module M {

  array A = [3] U32

  constant a = 0

  enum E { X, Y, Z }

  type T

  type Alias = T

  struct S { x: U32 }

  port P

  state machine S

  passive component C {
    type T
    type Alias = T
    array A = [3] U32
    constant a = 0
    enum E { X, Y, Z }
    dictionary enum E2 { A, B, C }
    struct S { x: U32 }
    dictionary struct S2 { a: U32 }
    state machine S
  }

  instance c: C base id 0x100

  topology T {

  }

}
