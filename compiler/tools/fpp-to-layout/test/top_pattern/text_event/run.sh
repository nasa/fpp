pattern_text_event()
{
  run_test "" pattern_text_event && \
    compare M_T TextEvents && \
    compare_out pattern_text_event
}
