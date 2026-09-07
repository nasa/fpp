type T = U32
state machine M {
  type T = bool
  signal s1: U32
  signal s2: T
  guard g
  initial enter S
  state S {
    initial enter T
    choice C { if g enter T else enter T }
    on s1 enter C
    on s2 enter C
    state T
  }
}
