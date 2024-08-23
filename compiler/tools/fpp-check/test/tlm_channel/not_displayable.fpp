module Fw {
  port Time
  port Tlm
}

struct S1 { x: S2 }

struct S2 { x: A1 }

array A1 = [3] A2

array A2 = [3] T

type T

passive component C {
  telemetry port tlmOut
  time get port timeGetOut
  telemetry C: S1
}
