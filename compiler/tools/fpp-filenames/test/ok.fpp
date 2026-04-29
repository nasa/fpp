port P

array A = [3] U32
constant a = 0
enum E { X, Y }
struct S { x: U32 }
state machine SM1
state machine SM2 {
  array A = [3] U32
  initial enter S
  constant a = 0
  enum E { X, Y }
  struct S { x: U32 }
  type X = S
  state S
}

type FwOpcodeType = U32
type WithCDefinition = U32
type WithCDefinitionBuiltin = FwOpcodeType
type WithoutCDefinition = A

module M {

  interface I {
    sync input port p: P
  }

  passive component C {
    array A = [3] U32
    constant a = 0
    enum E { X, Y }
    struct S { x: U32 }
    import I
    state machine SM {
      array A = [3] U32
      state S
      initial enter S
    }
  }

}

topology T {
  telemetry packets P {

  }
}
