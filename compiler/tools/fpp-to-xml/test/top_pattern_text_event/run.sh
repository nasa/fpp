pattern_text_event()
{
  run_test "-p $PWD" pattern_text_event && \
    diff_xml TimePort LogTextPort TextEventsComponent CComponent TTopologyApp
}
