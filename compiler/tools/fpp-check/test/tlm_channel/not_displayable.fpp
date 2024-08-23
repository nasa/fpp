module Fw {
  port Time
  port Tlm
}

type T

array A1 = [3] A2

array A2 = [3] T

struct S1 { x: S2 }

struct S2 { x: A1 }

passive component C {
  telemetry port tlmOut
  time get port timeGetOut
  telemetry C: S1
}
