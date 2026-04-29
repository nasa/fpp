locate state machine SM at "state_machine_ok.fpp"
state machine SM

locate state machine SM.c at "state_machine_ok.fpp"
module M {
  state machine SM
}

locate state machine C.SM at "state_machine_ok.fpp"
passive component C {
  state machine SM
}

