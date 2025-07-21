pattern_command_list()
{
  run_test "" pattern_command_list && \
    compare M_T Command && \
    compare M_T CommandRegistration && \
    compare M_T CommandResponse && \
    compare_out pattern_command_list
}
