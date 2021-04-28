pattern_time()
{
  run_test "-p $PWD" pattern_time && \
    diff_xml TimePort TimeComponent CComponent TTopologyApp
}
