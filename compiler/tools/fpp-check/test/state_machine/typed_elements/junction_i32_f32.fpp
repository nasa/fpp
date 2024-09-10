state machine M {
  signal s1: I32
  signal s2: F32
  guard g
  initial enter S
  state S {
    initial enter T
    junction J { if g enter T else enter T }
    on s1 enter J
    on s2 enter J
    state T
  }
}
