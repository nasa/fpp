import_merge()
{
  run_test "-p $PWD" import_merge && \
    diff_xml PPort AComponent BComponent TTopologyApp
}
