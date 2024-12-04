state machine M {
  guard g
  initial enter S
  state S {
    state T
    initial enter C
    choice C { if g enter T else enter T }
  }
}
