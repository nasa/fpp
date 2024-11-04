state machine M {
  guard g
  state S {
    initial enter T
    state T
  }
  initial enter J1
  choice J1 { if g enter J2 else enter S }
  choice J2 { if g enter S.T else enter S }
}
