type T = U32

module Fw {
  port Tlm
  port Time
}

passive component C {
  array A = [3] U32
  type T = A
  type T2 = string size 30

  telemetry port tlmOut
  time get port timeGet

  telemetry E1: T
  telemetry E2: T2
}
