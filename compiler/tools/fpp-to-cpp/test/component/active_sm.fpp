module FppTest {

  active component ActiveSmInitial {

    include "../state-machine/initial/include/Basic.fpp"

    state machine instance basic: Basic

    state machine instance smInitialBasic: SmInitial.Basic

    include "../state-machine/initial/include/Junction.fpp"

    state machine instance $junction: Junction

    state machine instance smInitialJunction: SmInitial.Junction

  }

}
