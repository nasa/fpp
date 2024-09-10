state machine M {
  action a: U32
  initial enter S
  state S {
    exit do { a }
  }
}
