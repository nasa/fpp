state machine M {
  signal s1: U32
  signal s2: bool
  guard g
  initial enter S
  state S {
    initial enter T
    choice J { if g enter J1 else enter T }
    choice J1 { if g enter T else enter T }
    on s1 enter J
    on s2 enter J1
    state T
  }
}
