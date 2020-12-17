commands()
{
  update "-p $PWD -i ports.fpp" commands
  move_xml CommandsComponent
}

empty()
{
  update "-p $PWD" empty
  move_xml EmptyComponent
}

general_ports()
{
  update "-p $PWD -i ports.fpp" general_ports
  move_xml GeneralPorts1Component GeneralPorts2Component
}

internal_ports()
{
  update "-p $PWD -i ports.fpp" internal_ports
  move_xml InternalPortsComponent
}

ports()
{
  update "-p $PWD" ports
  move_xml PPort \
    CmdPort CmdRegPort CmdResponsePort \
    LogPort LogTextPort \
    PrmGetPort PrmSetPort \
    TimePort \
    TlmPort
}

special_ports()
{
  update "-p $PWD -i ports.fpp" special_ports
  move_xml SpecialPortsComponent
}
