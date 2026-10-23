module template T {
  state machine M {
    state S
    initial enter S
  }
}

expand T

# Verify that M.State is visible after expanding T
type X = M.State
