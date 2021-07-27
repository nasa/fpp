# Placeholders for built-in ports
module Fw {
  port BufferSend
  port Cmd
  port CmdReg
  port CmdResponse
  port Log
  port LogText
  port PrmGet
  port PrmSet
  port Time
  port Tlm
}

module Svc {
  port Ping
  port Sched
  passive component Time {

  }
}
