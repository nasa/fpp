module FppTest {

  active component ActiveSmInitial {

    include "../state-machine/initial/include/Basic.fpp"

    state machine instance basic: Basic

    state machine instance smInitialBasic: SmInitial.Basic

  }

}
