numbering_general()
{
  run_test "-p $PWD" numbering_general && \
    diff_xml PPort SourceComponent TargetComponent TTopologyApp
}
