module Fw {
  port Time
  port Tlm
}

passive component C {
  telemetry port tlmOut
  time get port timeGetOut
  telemetry T: U32
}

instance c1: C base id 0x100
instance c2: C base id 0x100
