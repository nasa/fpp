pattern_event()
{
  run_test "" pattern_event && \
    compare M_T Events && \
    compare_out pattern_event
}
