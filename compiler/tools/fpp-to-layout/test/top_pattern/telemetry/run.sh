pattern_param()
{
  run_test "" pattern_param && \
    compare M_T Parameters && \
    compare_out pattern_param
}
