state machine M {
  action a: U32
  guard g
  initial enter J
  junction J { if g do { a } enter S else enter S }
  state S
}
