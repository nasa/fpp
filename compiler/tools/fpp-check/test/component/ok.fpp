# Placeholders for built-in ports
module Fw {
  port Cmd
  port CmdReg
  port CmdResponse
  port Log
  port LogText
  port PrmGet
  port PrmSet
  port Tlm
}

port P

active component C {

  constant a = 0

  array A = [3] U32

  struct S { x: U32 }

  enum E { X }

  async input port p1: P
  sync input port p2: P
  guarded input port p3: P
  output port p4: P

  command recv port cmdIn
  command reg port cmdRegIn
  command resp port cmdResponseIn

  event port eventOut
  text event port textEventOut

  param get port paramGetOut
  param set port paramSetOut

  telemetry port tlmOut

  state machine S
  state machine instance s: S

}
