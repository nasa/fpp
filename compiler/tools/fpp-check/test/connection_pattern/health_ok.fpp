module Svc {
  port Ping
}
module M {
  passive component Health {
    sync input port pingIn: Svc.Ping
    output port pingOut: Svc.Ping
  }
  passive component C {
    sync input port pingIn: Svc.Ping
    output port pingOut: Svc.Ping
  }
  instance $health: Health base id 0x100
  instance c: C base id 0x100
  topology T {
    instance $health
    instance c
    health connections instance $health { c }
  }
}
