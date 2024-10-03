module FppTest {

  module SmState {

    @ A basic state machine with a string guard
    state machine BasicGuardString { 

      @ Action a
      action a: string

      @ Guard g
      guard g: string

      @ Signal s
      signal s: string

      initial enter S

      @ State S
      state S {

        @ State transition
        on s if g do { a } enter T

      }

      @ State T
      state T

    }

  }

}
