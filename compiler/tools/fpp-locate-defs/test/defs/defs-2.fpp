module M {

  array A = [3] U32

  constant a = 0

  enum E { X, Y, Z }

  type T

  struct S { x: U32 }

  port P

  state machine LedSm

  passive component C {
    type T
    array A = [3] U32
    constant a = 0
    enum E { X, Y, Z }
    struct S { x: U32 }
    state machine LedSm
    state machine instance led1: LedSm
  }

  instance c: C base id 0x100

  topology T {

  }

}
