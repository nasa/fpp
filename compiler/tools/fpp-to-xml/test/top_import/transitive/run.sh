import_transitive()
{
  run_test "-p $PWD" import_transitive && \
    diff_xml PPort AComponent BComponent TTopologyApp
}
