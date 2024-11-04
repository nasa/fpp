state machine M {
  guard g
  signal s
  initial enter S
  state S {
    on s enter J
    choice J { if g enter S1 else enter S2 }
  }
  state S1 {
    on s enter S
  }
  state S2
}
