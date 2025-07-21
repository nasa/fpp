pattern_command()
{
  run_test "" pattern_command && \
    compare M_T Command && \
    compare M_T CommandRegistration && \
    compare M_T CommandResponse && \
    compare_out pattern_command
}
