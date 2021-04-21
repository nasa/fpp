pattern_health_list()
{
  update "-p $PWD" pattern_health_list
  move_xml PingPort HealthComponent CComponent TTopologyApp
}
