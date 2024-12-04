state machine M {
  guard g: U32
  initial enter S
  state S {
    initial enter C
    choice C { if g enter T else enter T }
    state T
  }
}
