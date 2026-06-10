module Svc {
  port Ping(key: U32)
}
module Svc {
  passive component Health {
    sync input port pingIn: [2] Svc.Ping
    output port pingOut: [2] Svc.Ping
    match pingOut with pingIn
  }
}
module M {

  passive component C {
    sync input port pingIn: Svc.Ping
    output port pingOut: Svc.Ping
  }

}
