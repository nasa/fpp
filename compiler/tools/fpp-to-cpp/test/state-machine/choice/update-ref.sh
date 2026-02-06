. ./fpp-flags.sh

basic()
{
  update "$fpp_flags" Basic && \
  move_cpp BasicStateMachine && \
  move_cpp Basic_StateEnum
}

basic_u32()
{
  update "$fpp_flags" BasicU32 && \
  move_cpp BasicU32StateMachine && \
  move_cpp BasicU32_StateEnum
}

choice_to_choice()
{
  update "$fpp_flags" ChoiceToChoice && \
  move_cpp ChoiceToChoiceStateMachine && \
  move_cpp ChoiceToChoice_StateEnum
} 

choice_to_state()
{
  update "$fpp_flags" ChoiceToState && \
  move_cpp ChoiceToStateStateMachine && \
  move_cpp ChoiceToState_StateEnum
}

input_pair_u16_u32()
{
  update "$fpp_flags" InputPairU16U32
  move_cpp InputPairU16U32StateMachine
}

sequence()
{
  update "$fpp_flags" Sequence
  move_cpp SequenceStateMachine
}

sequence_u32()
{
  update "$fpp_flags" SequenceU32
  move_cpp SequenceU32StateMachine
}
