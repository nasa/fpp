locate state machine StateMachines.Bar at "StateMachines.fpp"

module DefinitionsAndSpecifiers {

  port Port1(
          a: U32
          b: F64
          )

  active component C {

    async input port dataIn: Port1

    state machine Foo

    state machine instance foo1: Foo

    state machine instance foo2: Foo

    state machine instance bar: StateMachines.Bar

  }

}

  
