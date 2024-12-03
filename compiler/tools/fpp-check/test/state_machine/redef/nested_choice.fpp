state machine M {
  guard g
  state S {
    state T
    choice C { if g enter S else enter S }
    choice C { if g enter S else enter S }
  }
}
