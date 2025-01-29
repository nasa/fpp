pattern_health()
{
  run_test "" pattern_health && \
    compare T Health && \
    compare_out pattern_health
}
