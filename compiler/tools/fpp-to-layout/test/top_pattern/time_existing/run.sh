pattern_time_existing()
{
  run_test "" pattern_time_existing && \
    compare T Time && \
    compare_out pattern_time_existing
}
