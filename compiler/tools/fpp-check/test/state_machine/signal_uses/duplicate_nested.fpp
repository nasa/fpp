state machine M {
  signal s
  initial enter S
  state S {
    initial enter T
    state T {
      on s enter T
      on s enter T
    }
  }
}
