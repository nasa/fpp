module M {
    state machine S
}

state machine SO

active component C {

  state machine S

  state machine instance s1: M.S
  state machine instance s2: SO
  state machine instance s3: S

}
