include "../../state-machine/junction/include/Basic.fppi"

state machine instance basic: Basic

state machine instance smJunctionBasic: SmJunction.Basic priority 1 assert

state machine instance smJunctionBasicU32: SmJunction.BasicU32 priority 2 block

state machine instance smJunctionInputPairU16U32: SmJunction.InputPairU16U32 priority 3 drop

state machine instance smJunctionJunctionToJunction: SmJunction.JunctionToJunction priority 4 hook

state machine instance smJunctionJunctionToState: SmJunction.JunctionToState

state machine instance smJunctionSequence: SmJunction.Sequence

state machine instance smJunctionSequenceU32: SmJunction.SequenceU32
