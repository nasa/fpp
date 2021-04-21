module Svc {
  port Ping
}
module M {
  passive component Health {
    sync input port pingIn: [2] Svc.Ping
    output port pingOut: [2] Svc.Ping
    match pingOut with pingIn
  }
  passive component C {
    sync input port pingIn: Svc.Ping
    output port pingOut: Svc.Ping
  }
  instance $health: Health base id 0x100
  instance c1: C base id 0x200
  instance c2: C base id 0x300
  topology T {
    instance $health
    instance c1
    instance c2
    health connections instance $health
  }
}
