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

state machine instance smStateBasicInternal: SmState.BasicInternal

state machine instance smStateBasicSelf: SmState.BasicSelf

state machine instance smStateBasicString: SmState.BasicString

state machine instance smStateBasicTestAbsType: SmState.BasicTestAbsType

state machine instance smStateBasicTestArray: SmState.BasicTestArray

state machine instance smStateBasicTestEnum: SmState.BasicTestEnum

state machine instance smStateBasicTestStruct: SmState.BasicTestStruct

state machine instance smStateBasicU32: SmState.BasicU32
