state machine M {
  guard g
  signal s
  initial enter S
  state S
  junction J { if g enter S else enter S }
}
