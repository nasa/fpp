pattern_time_existing()
{
  run_test "" pattern_time_existing && \
    compare M_T Time && \
    compare_out pattern_time_existing
}
