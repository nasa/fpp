state machine M {
  guard g: U32
  initial enter C
  choice C { if g enter S else enter S }
  state S
}
