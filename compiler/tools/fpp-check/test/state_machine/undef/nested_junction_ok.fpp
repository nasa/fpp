state machine M {
  guard g
  initial enter S
  state S {
    state T
    initial enter J
    junction J { if g enter T else enter T }
  }
}
