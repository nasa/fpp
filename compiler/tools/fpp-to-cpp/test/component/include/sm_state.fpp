include "../../state-machine/state/include/Basic.fpp"

state machine instance basic: Basic

state machine instance smStateBasic: SmState.Basic priority 1 assert

state machine instance smStateBasicGuard: SmState.BasicGuard priority 2 block

state machine instance smStateBasicGuardString: SmState.BasicGuardString priority 3 drop
