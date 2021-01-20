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

events()
{
  update "-p $PWD -i ports.fpp" events
  move_xml EventsComponent
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

parameters()
{
  update "-p $PWD -i ports.fpp" parameters
  move_xml ParametersComponent
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

telemetry()
{
  update "-p $PWD -i ports.fpp" telemetry
  move_xml TelemetryComponent
}

types()
{
  update "-p $PWD -n types.names.txt" types
  mv types.names.txt types.names.ref.txt
  move_xml TypesComponent \
    Types_AArray \
    Types_EEnum \
    Types_SSerializable \
    Types_AUseArray \
    Types_EUseArray \
    Types_SUseArray \
    Types_TUseArray
} 
