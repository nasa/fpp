pattern_param()
{
  run_test "-p $PWD" pattern_param && \
    diff_xml PrmGetPort PrmSetPort ParametersComponent CComponent TTopologyApp
}
