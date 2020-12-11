empty()
{
  update "-p $PWD" empty
  move_xml EmptyComponent
}

general_ports()
{
  update "-p $PWD -i port.fpp" general_ports
  move_xml GeneralPorts1Component GeneralPorts2Component
}

port()
{
  update "-p $PWD" port
  move_xml PPort \
    CmdPort CmdRegPort CmdResponsePort \
    LogPort LogTextPort \
    PrmGetPort PrmSetPort \
    TimePort \
    TlmPort
}

special_ports()
{
  update "-p $PWD -i port.fpp" special_ports
  move_xml SpecialPortsComponent
}
