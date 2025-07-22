pattern_health_list()
{
  run_test "" pattern_health_list && \
    compare M_T Health && \
    compare_out pattern_health_list
}
