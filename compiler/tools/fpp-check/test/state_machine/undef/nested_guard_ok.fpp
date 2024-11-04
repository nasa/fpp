state machine M {
  guard g
  initial enter S
  state S {
    initial enter J
    choice J { if g enter T else enter T }
    state T
  }
}
