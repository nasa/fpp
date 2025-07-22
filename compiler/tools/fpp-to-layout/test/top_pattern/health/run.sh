pattern_health()
{
  run_test "" pattern_health && \
    compare M_T Health && \
    compare_out pattern_health
}
