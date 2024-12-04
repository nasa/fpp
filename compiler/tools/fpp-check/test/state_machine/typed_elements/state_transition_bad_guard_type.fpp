state machine M {
  signal s
  guard g: U32
  initial enter S
  state S {
    on s if g enter S
  }
}
