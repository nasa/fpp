state machine M {
  guard g
  initial enter S
  state S {
    initial enter C
    choice C { if g enter T else enter T }
    state T
  }
}
