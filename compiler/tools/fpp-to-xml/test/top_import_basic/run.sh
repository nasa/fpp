import_basic()
{
  run_test "-p $PWD" import_basic && \
    diff_xml PPort AComponent BComponent TTopologyApp
}
