state machine M {
  action a: U32
  guard g
  initial enter S
  state S {
    initial enter C
    choice C { if g enter T else do { a } enter T }
    state T
  }
}
