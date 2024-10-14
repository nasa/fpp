module FppTest {

  active component ActiveSmInitial {

    include "../state-machine/initial/include/Basic.fpp"

    state machine instance basic: Basic

    state machine instance smInitialBasic: SmInitial.Basic

    include "../state-machine/initial/include/Junction.fpp"

    state machine instance $junction: Junction priority 1 assert

    state machine instance smInitialJunction: SmInitial.Junction priority 2 block

    include "../state-machine/initial/include/Nested.fpp" 

    state machine instance nested: Nested priority 3 drop

    state machine instance smInitialNested: SmInitial.Nested priority 4 hook

  }

  active component ActiveSmState {

    include "../state-machine/state/include/Basic.fpp"

    state machine instance basic: Basic

    state machine instance smStateBasic: SmState.Basic priority 1 assert

    state machine instance smStateBasicGuard: SmState.BasicGuard priority 2 block

    state machine instance smStateBasicGuardString: SmState.BasicGuardString priority 3 drop

  }

}