pattern_time()
{
  run_test "" pattern_time && \
    compare M_T Time && \
    compare_out pattern_time
}
