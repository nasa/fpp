state machine M {
  guard g
  state S {
    state T
    choice J { if g enter S else enter S }
    choice J { if g enter S else enter S }
  }
}
