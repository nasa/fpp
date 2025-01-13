pattern_time()
{
  run_test "" pattern_time && \
    compare T Time && \
    compare_out pattern_time
}
