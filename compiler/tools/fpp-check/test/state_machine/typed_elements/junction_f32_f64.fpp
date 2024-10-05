state machine M {
  signal s1: F32
  signal s2: F64
  guard g: F64
  initial enter S
  state S {
    initial enter T
    junction J { if g enter T else enter T }
    on s1 enter J
    on s2 enter J
    state T
  }
}
