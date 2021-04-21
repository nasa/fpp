pattern_event_list()
{
  run_test "-p $PWD" pattern_event_list && \
    diff_xml TimePort LogPort EventsComponent CComponent TTopologyApp
}
