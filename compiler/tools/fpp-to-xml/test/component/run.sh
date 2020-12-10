empty()
{
  run_test "-p $PWD" empty && \
    diff_xml EmptyComponent
}

general_ports()
{
  run_test "-p $PWD -i port.fpp" general_ports && \
    diff_xml GeneralPorts1Component GeneralPorts2Component
}

port()
{
  run_test "-p $PWD" port && \
    diff_xml PPort
}
