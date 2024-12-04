state machine M {
  action a: U32
  guard g
  initial enter C
  choice C { if g enter S else do { a } enter S }
  state S
}
