module FppTest {

  module SmState {

    @ A basic state machine
    state machine Basic {

      @ Action a
      action a

      @ Signal s
      signal s

      initial enter S

      @ State S
      state S {
        @ A state transition
        on s do { a } enter T
      }

      @ State T
      state T

    }

  }

}
