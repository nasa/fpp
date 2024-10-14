include "../../state-machine/initial/include/Basic.fppi"

state machine instance basic: Basic

state machine instance smInitialBasic: SmInitial.Basic

include "../../state-machine/initial/include/Junction.fppi"

state machine instance $junction: Junction priority 1 assert

state machine instance smInitialJunction: SmInitial.Junction priority 2 block

include "../../state-machine/initial/include/Nested.fppi" 

state machine instance nested: Nested priority 3 drop

state machine instance smInitialNested: SmInitial.Nested priority 4 hook
