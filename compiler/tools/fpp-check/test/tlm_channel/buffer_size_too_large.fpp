constant FW_TLM_BUFFER_MAX_SIZE = 8

module Fw {

  port Time
  port Tlm

}

struct S { x: U32, y: F64 }

passive component C {

  telemetry port tlmOut
  time get port timeGetOut

  telemetry T: S

}
