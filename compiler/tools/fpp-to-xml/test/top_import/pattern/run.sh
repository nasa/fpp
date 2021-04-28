import_pattern()
{
  run_test "-p $PWD" import_pattern && \
    diff_xml TimePort TimeComponent CComponent TTopologyApp
}
