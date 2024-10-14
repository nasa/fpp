include "../../state-machine/state/include/Basic.fppi"

state machine instance basic: Basic

state machine instance smStateBasic: SmState.Basic priority 1 assert

state machine instance smStateBasicGuard: SmState.BasicGuard priority 2 block

state machine instance smStateBasicGuardString: SmState.BasicGuardString priority 3 drop

state machine instance smStateBasicGuardTestAbsType: SmState.BasicGuardTestAbsType priority 4 hook

state machine instance smStateBasicGuardTestArray: SmState.BasicGuardTestArray

state machine instance smStateBasicGuardTestEnum: SmState.BasicGuardTestEnum

state machine instance smStateBasicGuardTestStruct: SmState.BasicGuardTestStruct

state machine instance smStateBasicGuardU32: SmState.BasicGuardU32
