state machine M {
  action a: I16
  signal s1: I16
  signal s2: I32
  guard g
  initial enter S
  state S {
    initial enter T
    junction J { if g do { a } enter T else enter T }
    on s1 enter J
    on s2 enter J
    state T
  }
}
