general_ports()
{
  update "-p $PWD -i port.fpp" general_ports
  move_xml GeneralPorts1Component GeneralPorts2Component GeneralPorts3Component
}

port()
{
  update "-p $PWD" port
  move_xml PPort
}
