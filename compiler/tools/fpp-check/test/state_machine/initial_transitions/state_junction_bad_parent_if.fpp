state machine M {
  guard g
  initial enter S
  state S {
    state T {
      initial enter U
      state U
    }
    initial enter J1
    junction J1 { if g enter J2 else enter T }
    junction J2 { if g enter T.U else enter T }
  }
}
