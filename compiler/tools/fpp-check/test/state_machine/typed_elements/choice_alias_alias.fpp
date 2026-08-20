@ At a choice, two distinct alias types sharing the same underlying primitive
@ resolve via their underlying types (spec Type-Options, common type rule 2.2).
@ alias A = I32 and alias B = I32 both reduce to I32: accepted.
type A = I32
type B = I32

state machine M {
  signal s1: A
  signal s2: B

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
