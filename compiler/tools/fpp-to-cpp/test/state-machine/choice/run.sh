. ./fpp-flags.sh

basic()
{
  run_test "$fpp_flags" Basic && \
    diff_cpp BasicStateMachine
}

basic_u32()
{
  run_test "$fpp_flags" BasicU32 && \
    diff_cpp BasicU32StateMachine
}

choice_to_choice()
{
  run_test "$fpp_flags" ChoiceToChoice && \
    diff_cpp ChoiceToChoiceStateMachine
}

choice_to_state()
{
  run_test "$fpp_flags" ChoiceToState && \
    diff_cpp ChoiceToStateStateMachine
}

input_pair_u16_u32()
{
  run_test "$fpp_flags" InputPairU16U32 && \
    diff_cpp InputPairU16U32StateMachine
}

sequence()
{
  run_test "$fpp_flags" Sequence && \
    diff_cpp SequenceStateMachine
}

sequence_u32()
{
  run_test "$fpp_flags" SequenceU32 && \
    diff_cpp SequenceU32StateMachine
}
