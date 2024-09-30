numbering_matched()
{
  run_test "-p $PWD" unmatched_connections && \
    diff_xml PPort C1Component C2Component TTopologyApp
}
