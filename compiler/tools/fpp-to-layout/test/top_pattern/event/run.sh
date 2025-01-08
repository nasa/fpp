pattern_event()
{
  run_test "" pattern_event && \
    compare T Events && \
    compare_out pattern_event
}
