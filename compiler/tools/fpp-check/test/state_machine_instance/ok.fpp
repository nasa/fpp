module M {
  state machine S
}

state machine T

active component C {

  state machine S

  state machine instance s1: M.S
  state machine instance s2: T
  state machine instance s3: S

}
