include "../../state-machine/choice/include/Basic.fppi"

state machine instance basic: Basic

state machine instance smChoiceBasic: SmChoice.Basic priority 1 assert

state machine instance smChoiceBasicU32: SmChoice.BasicU32 priority 2 block

state machine instance smChoiceInputPairU16U32: SmChoice.InputPairU16U32 priority 3 drop

state machine instance smChoiceChoiceToChoice: SmChoice.ChoiceToChoice priority 4 hook

state machine instance smChoiceChoiceToState: SmChoice.ChoiceToState

state machine instance smChoiceSequence: SmChoice.Sequence

state machine instance smChoiceSequenceU32: SmChoice.SequenceU32
