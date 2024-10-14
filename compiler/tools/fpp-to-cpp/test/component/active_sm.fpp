module FppTest {

  active component ActiveSmInitial {

    include "../state-machine/initial/include/Basic.fpp"

    state machine instance basic: Basic

    state machine instance smInitialBasic: SmInitial.Basic

    include "../state-machine/initial/include/Junction.fpp"

    state machine instance $junction: Junction

    state machine instance smInitialJunction: SmInitial.Junction

    include "../state-machine/initial/include/Nested.fpp"

    state machine instance nested: Nested

    state machine instance smInitialNested: SmInitial.Nested

  }

}
