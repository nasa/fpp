state machine M {
  guard g: U32
  initial enter S
  state S {
    initial enter J
    choice J { if g enter T else enter T }
    state T
  }
}
