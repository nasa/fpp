state machine M {
  guard g: U32
  initial enter S
  state S {
    initial enter J
    junction J { if g enter T else enter T }
    state T
  }
}
