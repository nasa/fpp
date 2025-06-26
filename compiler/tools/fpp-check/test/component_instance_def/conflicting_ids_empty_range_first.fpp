module Fw {
  port Time
  port Tlm
}

passive component C1 {

}

passive component C2 {
  time get port timeGetOut
  telemetry port tlmOut
  telemetry T: U32
}

instance c1: C1 base id 0x100
instance c2: C2 base id 0x100
