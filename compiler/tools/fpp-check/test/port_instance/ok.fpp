# Placeholders for built-in ports
module Fw {
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

port P

active component C {

  async input port t1: [10] P priority 3 drop
  sync input port t2: P
  guarded input port t3: P
  output port t4: P

  async input port s1: [10] serial priority 3 drop
  sync input port s2: serial
  guarded input port s3: serial
  output port s4: serial

  command recv port cmdIn
  command reg port cmdRegIn
  command resp port cmdResponseIn
  event port eventOut
  param get port paramGetOut
  param set port paramSetOut
  telemetry port tlmOut
  text event port textEventOut
  time get port timeGetOut

}
