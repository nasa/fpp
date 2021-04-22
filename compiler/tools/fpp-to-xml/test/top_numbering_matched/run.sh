numbering_matched()
{
  run_test "-p $PWD" numbering_matched && \
    diff_xml PPort SourceComponent TargetComponent TTopologyApp
}
