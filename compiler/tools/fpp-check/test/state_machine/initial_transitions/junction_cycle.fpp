# Until we exclude junction cycles, this test should pass
# TODO: When we exclude junction cycles, we will need to update this test
state machine M {
  guard g
  initial enter J
  junction J { if g enter J else enter J }
}
