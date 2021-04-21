pattern_health_list()
{
  run_test "-p $PWD" pattern_health_list && \
    diff_xml PingPort HealthComponent CComponent TTopologyApp
}
