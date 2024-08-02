module M {

  @ State machine outside a component
  state machine SO
  @< State machine outside a component

  @ Component definition
  active component C {

    @ state machine instance
    state machine instance s: SO priority 1 blah
  }

}
