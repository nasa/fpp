state machine M {
  guard g
  state S {
    state T
    junction J { if g enter S else enter S }
    junction J { if g enter S else enter S }
  }
}
