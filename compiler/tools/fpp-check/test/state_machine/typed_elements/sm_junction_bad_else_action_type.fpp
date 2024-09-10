state machine M {
  action a: U32
  guard g
  initial enter J
  junction J { if g enter S else do { a } enter S }
  state S
}
