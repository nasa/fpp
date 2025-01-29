pattern_health_list()
{
  run_test "" pattern_health_list && \
    compare T Health && \
    compare_out pattern_health_list
}
