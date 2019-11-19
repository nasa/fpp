@ Port P
port P(x : U32)

@ Component C1
active component C1 {
  @ Constant n
  constant n = 5
  @ Constant p
  constant p = 10
  @ Port p1
  async input port p1 : P priority p
  @ Port p2
  guarded input port p2 : P
  @ Port p3
  internal input port p3 : P
  @ Port p4
  output port p4 : [ n ] P
  @ Port p5
  sync input port p5 : P
}

@ Component C2
passive component C2 {
  @ Port p1
  command port p1
  @ Port p2
  command reg port p2
  @ Port p3
  command resp port p3
  @ Port p4
  event port p4
  @ Port p5
  param get port p5
  @ Port p6
  param set port p6
  @ Port p7
  telemetry port p7
  @ Port p8
  time port p8
}

@ Component C3
queued component C3 {
  @ Port p1
  async input port p1 : P assert
  @ Port p2
  async input port P2 : P block
  @ Port p3
  async input port P3 : P drop
}
