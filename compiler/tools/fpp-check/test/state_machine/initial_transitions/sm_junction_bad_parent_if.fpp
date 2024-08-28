state machine M {
  guard g
  state S {
    initial enter T
    state T
  }
  initial enter J1
  junction J1 { if g enter J2 else enter S }
  junction J2 { if g enter S.T else enter S }
}
