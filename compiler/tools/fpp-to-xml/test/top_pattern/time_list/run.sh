pattern_time_list()
{
  run_test "-p $PWD" pattern_time_list && \
    diff_xml TimePort TimeComponent CComponent TTopologyApp
}
