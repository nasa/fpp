commands()
{
  run_test "-p $PWD -i port.fpp" commands && \
    diff_xml CommandsComponent
}

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

internal_ports()
{
  run_test "-p $PWD -i port.fpp" internal_ports && \
    diff_xml InternalPortsComponent
}

port()
{
  run_test "-p $PWD" port && \
    diff_xml PPort \
      CmdPort CmdRegPort CmdResponsePort \
      LogPort LogTextPort \
      PrmGetPort PrmSetPort \
      TimePort \
      TlmPort
}

special_ports()
{
  run_test "-p $PWD -i port.fpp" special_ports && \
    diff_xml SpecialPortsComponent
}
