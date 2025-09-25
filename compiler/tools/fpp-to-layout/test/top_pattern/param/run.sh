pattern_param_list()
{
  run_test "" pattern_param_list && \
    compare M_T Parameters && \
    compare_out pattern_param_list
}
