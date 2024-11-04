state machine M {
  initial enter S
  state S {
    initial enter J
    choice J { if g enter S else enter S }
  }
}
