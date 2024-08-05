module M {
  state machine S
}

active component C {
  state machine instance s: M.S priority "abc"
}
