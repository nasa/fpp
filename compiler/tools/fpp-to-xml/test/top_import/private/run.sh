import_private()
{
  run_test "-p $PWD" import_private && \
    diff_xml PPort AComponent BComponent TTopologyApp
}
