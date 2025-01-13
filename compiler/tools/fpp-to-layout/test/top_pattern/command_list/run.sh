pattern_command_list()
{
  run_test "" pattern_command_list && \
    compare T Command && \
    compare T CommandRegistration && \
    compare T CommandResponse && \
    compare_out pattern_command_list
}
