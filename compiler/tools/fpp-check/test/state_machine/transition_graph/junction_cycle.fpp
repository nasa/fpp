state machine M {
  guard g
  initial enter J1
  choice J1 { if g enter S else enter J2 }
  choice J2 { if g enter S else enter J1 }
  state S
}
