pattern_health()
{
  run_test "-p $PWD" pattern_health && \
    diff_xml PingPort HealthComponent CComponent TTopologyApp
}
