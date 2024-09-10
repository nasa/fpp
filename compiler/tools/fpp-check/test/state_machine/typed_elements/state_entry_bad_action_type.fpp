state machine M {
  action a: U32
  initial enter S
  state S {
    entry do { a }
  }
}
