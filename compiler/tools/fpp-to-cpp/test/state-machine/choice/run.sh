. ./fpp-flags.sh

basic()
{
  run_test "$fpp_flags" Basic && \
    diff_cpp BasicStateMachine && \
    diff_cpp Basic_StateEnum
}

basic_u32()
{
  run_test "$fpp_flags" BasicU32 && \
    diff_cpp BasicU32StateMachine && \
    diff_cpp BasicU32_StateEnum
}

choice_to_choice()
{
  run_test "$fpp_flags" ChoiceToChoice && \
    diff_cpp ChoiceToChoiceStateMachine && \
    diff_cpp ChoiceToChoice_StateEnum
}

choice_to_state()
{
  run_test "$fpp_flags" ChoiceToState && \
    diff_cpp ChoiceToStateStateMachine && \
    diff_cpp ChoiceToState_StateEnum
}

input_pair_u16_u32()
{
  run_test "$fpp_flags" InputPairU16U32 && \
    diff_cpp InputPairU16U32StateMachine && \
    diff_cpp InputPairU16U32_StateEnum
}

sequence()
{
  run_test "$fpp_flags" Sequence && \
    diff_cpp SequenceStateMachine && \
    diff_cpp Sequence_StateEnum
}

sequence_u32()
{
  run_test "$fpp_flags" SequenceU32 && \
    diff_cpp SequenceU32StateMachine && \
    diff_cpp SequenceU32_StateEnum
}
