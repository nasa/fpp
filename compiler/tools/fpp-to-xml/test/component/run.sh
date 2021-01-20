commands()
{
  run_test "-p $PWD -i ports.fpp" commands && \
    diff_xml CommandsComponent
}

empty()
{
  run_test "-p $PWD" empty && \
    diff_xml EmptyComponent
}

events()
{
  run_test "-p $PWD -i ports.fpp" events && \
    diff_xml EventsComponent
}

general_ports()
{
  run_test "-p $PWD -i ports.fpp" general_ports && \
    diff_xml GeneralPorts1Component GeneralPorts2Component
}

internal_ports()
{
  run_test "-p $PWD -i ports.fpp" internal_ports && \
    diff_xml InternalPortsComponent
}

parameters()
{
  run_test "-p $PWD -i ports.fpp" parameters && \
    diff_xml ParametersComponent
}

ports()
{
  run_test "-p $PWD" ports && \
    diff_xml PPort \
      CmdPort CmdRegPort CmdResponsePort \
      LogPort LogTextPort \
      PrmGetPort PrmSetPort \
      TimePort \
      TlmPort
}

special_ports()
{
  run_test "-p $PWD -i ports.fpp" special_ports && \
    diff_xml SpecialPortsComponent
}

telemetry()
{
  run_test "-p $PWD -i ports.fpp" telemetry && \
    diff_xml TelemetryComponent
}

types()
{
  run_test "-p $PWD -n types.names.txt" types && \
    diff -u types.names.txt types.names.ref.txt && \
    diff_xml TypesComponent \
      Types_AArray \
      Types_EEnum \
      Types_SSerializable \
      Types_AUseArray \
      Types_EUseArray \
      Types_SUseArray \
      Types_TUseArray
}
