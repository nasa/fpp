pattern_health()
{
  update "-p $PWD" pattern_health
  move_xml PingPort HealthComponent CComponent TTopologyApp
}
