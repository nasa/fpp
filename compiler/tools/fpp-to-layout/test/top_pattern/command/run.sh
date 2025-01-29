pattern_command()
{
  run_test "" pattern_command && \
    compare T Command && \
    compare T CommandRegistration && \
    compare T CommandResponse && \
    compare_out pattern_command
}
