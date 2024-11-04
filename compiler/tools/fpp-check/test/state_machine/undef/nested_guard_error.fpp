state machine M {
  initial enter S
  state S {
    initial enter C
    choice C { if g enter S else enter S }
  }
}
