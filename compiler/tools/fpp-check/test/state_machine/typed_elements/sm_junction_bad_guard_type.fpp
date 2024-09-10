state machine M {
  guard g: U32
  initial enter J
  junction J { if g enter S else enter S }
  state S
}
