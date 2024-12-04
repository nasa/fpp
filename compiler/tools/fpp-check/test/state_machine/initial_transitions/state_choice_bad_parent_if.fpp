state machine M {
  guard g
  initial enter S
  state S {
    state T {
      initial enter U
      state U
    }
    initial enter C1
    choice C1 { if g enter C2 else enter T }
    choice C2 { if g enter T.U else enter T }
  }
}
