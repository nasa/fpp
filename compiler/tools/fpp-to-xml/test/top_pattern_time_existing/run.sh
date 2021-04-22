pattern_time_existing()
{
  run_test "-p $PWD" pattern_time_existing && \
    diff_xml TimePort TimeComponent CComponent TTopologyApp
}
