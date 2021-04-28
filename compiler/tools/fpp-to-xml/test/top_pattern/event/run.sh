pattern_event()
{
  run_test "-p $PWD" pattern_event && \
    diff_xml TimePort LogPort EventsComponent CComponent TTopologyApp
}
