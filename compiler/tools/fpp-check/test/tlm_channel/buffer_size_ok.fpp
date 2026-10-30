type FwSizeStoreType = U16

constant FW_TLM_BUFFER_MAX_SIZE = 2

module Fw {

  port Time
  port Tlm

}

passive component C {

  telemetry port tlmOut
  time get port timeGetOut

  @ At least 2 bytes, for an empty string
  telemetry T: string size 100

}
