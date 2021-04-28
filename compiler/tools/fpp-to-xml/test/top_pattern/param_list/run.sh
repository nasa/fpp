pattern_param_list()
{
  run_test "-p $PWD" pattern_param_list && \
    diff_xml PrmGetPort PrmSetPort ParametersComponent CComponent TTopologyApp
}
