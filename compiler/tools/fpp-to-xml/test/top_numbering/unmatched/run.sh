numbering_unmatched()
{
  run_test "-p $PWD" numbering_unmatched && \
    diff_xml PPort C1Component TTopologyApp
}
