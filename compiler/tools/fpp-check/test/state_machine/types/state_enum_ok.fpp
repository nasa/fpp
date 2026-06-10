state machine SM {
  signal s
  initial enter S1
  state S1 {
    initial enter S2
    state S2 {
      on s enter S3
    }
  }
  state S3
}

type E = SM.State
constant a = SM.State.__FPRIME_UNINITIALIZED
constant b = SM.State.S1_S2
constant c = SM.State.S3
