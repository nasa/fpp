state machine M {
  guard g
  state S {
    initial enter T
    state T
  }
  initial enter C1
  choice C1 { if g enter C2 else enter S }
  choice C2 { if g enter S.T else enter S }
}
