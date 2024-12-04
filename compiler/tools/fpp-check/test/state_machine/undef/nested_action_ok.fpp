state machine M {
  action a
  initial enter S
  state S {
    initial do { a } enter T
    state T
  }
}
