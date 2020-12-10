ok()
{
  run_test "-p $PWD -i port.fpp" ok && \
    diff_xml C1Component C2Component C3Component
}

port()
{
  run_test "-p $PWD" port && \
    diff_xml PPort
}
