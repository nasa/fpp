pattern_text_event()
{
  run_test "" pattern_text_event && \
    compare T TextEvents && \
    compare_out pattern_text_event
}
