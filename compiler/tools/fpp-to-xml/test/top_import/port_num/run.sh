import_port_num()
{
  run_test "-p $PWD" import_port_num && \
    diff_xml PPort AComponent BComponent TTopologyApp
}
