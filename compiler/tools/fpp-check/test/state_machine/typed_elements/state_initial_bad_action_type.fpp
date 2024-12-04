state machine M {
  action a: U32
  initial enter S
  state S {
    state T
    initial do { a } enter T
  }
}
