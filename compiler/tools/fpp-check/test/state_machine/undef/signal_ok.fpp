state machine M {
  signal s
  initial enter S
  state S { on s enter S }
}
