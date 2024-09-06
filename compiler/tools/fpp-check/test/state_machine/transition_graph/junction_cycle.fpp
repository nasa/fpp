state machine M {
  guard g
  initial enter J1
  junction J1 { if g enter S else enter J2 }
  junction J2 { if g enter S else enter J1 }
  state S
}
