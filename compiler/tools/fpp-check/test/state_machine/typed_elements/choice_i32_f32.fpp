state machine M {
  signal s1: I32
  signal s2: F32
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
