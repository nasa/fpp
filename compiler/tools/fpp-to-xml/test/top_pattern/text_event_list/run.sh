pattern_text_event_list()
{
  run_test "-p $PWD" pattern_text_event_list && \
    diff_xml TimePort LogTextPort TextEventsComponent CComponent TTopologyApp
}
