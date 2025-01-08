pattern_event_list()
{
  run_test "" pattern_event_list && \
    compare T Events && \
    compare_out pattern_event_list
}
