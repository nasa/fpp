state machine M {
  guard g
  initial enter S
  state S {
    state T {
      initial enter U
      state U
    }
    initial enter J1
    choice J1 { if g enter J2 else enter T }
    choice J2 { if g enter T else enter S }
  }
}
