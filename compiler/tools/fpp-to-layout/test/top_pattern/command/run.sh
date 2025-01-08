pattern_command()
{
  run_test "" pattern_command && \
    compare TPatternCommand Command && \
    compare TPatternCommand CommandRegistration && \
    compare TPatternCommand CommandResponse && \
    compare_out pattern_command
}
