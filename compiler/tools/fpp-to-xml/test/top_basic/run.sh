basic()
{
  run_test "-p $PWD" basic && \
    diff_xml PPort C1Component C2Component TTopologyApp
}
