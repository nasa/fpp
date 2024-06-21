locate state machine StateMachines.S at "StateMachines.fpp"

module DefinitionsAndSpecifiers {

  port Port1(
          a: U32
          b: F64
          )

  active component C {

    async input port dataIn: Port1

    state machine S

    state machine instance s1: S

    state machine instance s2: S

    state machine instance s3: StateMachines.S
  }
}
