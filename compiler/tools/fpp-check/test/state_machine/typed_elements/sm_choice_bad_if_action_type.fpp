state machine M {
  action a: U32
  guard g
  initial enter C
  choice C { if g do { a } enter S else enter S }
  state S
}
