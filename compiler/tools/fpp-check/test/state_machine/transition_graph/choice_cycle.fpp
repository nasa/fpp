state machine M {
  guard g
  initial enter C1
  choice C1 { if g enter S else enter C2 }
  choice C2 { if g enter S else enter C1 }
  state S
}
