@ Number of health ping ports
constant numPingPorts = 10

queued component Health {

  @ Ping output port
  output port pingOut: [numPingPorts] Svc.Ping

  @ Ping input port
  async input port pingIn: [numPingPorts] Svc.Ping

  @ Corresponding port numbers of pingOut and pingIn must match
  match pingOut with pingIn

}

module Svc{
  port Ping
}
