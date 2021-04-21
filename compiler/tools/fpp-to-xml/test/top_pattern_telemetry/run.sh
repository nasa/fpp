pattern_telemetry()
{
  run_test "-p $PWD" pattern_telemetry && \
    diff_xml TimePort TlmPort TelemetryComponent CComponent TTopologyApp
}
