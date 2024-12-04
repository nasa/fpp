state machine M {
  signal s
  guard g
  action a: U32
  initial enter S
  state S {
    on s if g do { a }
  }
}
