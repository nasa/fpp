@ At a choice, an alias-typed transition and a primitive-typed transition must
@ resolve via the underlying types (spec Type-Options, common type rule 2.2).
@ alias A = I16 and I32 are both signed ints, so they widen to I32: accepted.
type A = I16

state machine M {
  signal s1: A
  signal s2: I32

  guard g
  initial enter S

  state S {

    initial enter T

    choice C {
      if g enter T else enter T
    }

    on s1 enter C
    on s2 enter C

    state T

  }
}
