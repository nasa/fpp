pattern_telemetry_list()
{
  run_test "-p $PWD" pattern_telemetry_list && \
    diff_xml TimePort TlmPort TelemetryComponent CComponent TTopologyApp
}
