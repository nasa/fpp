pattern_text_event_list()
{
  run_test "" pattern_text_event_list && \
    compare M_T TextEvents && \
    compare_out pattern_text_event_list
}
