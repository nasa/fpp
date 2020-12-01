module Fw {

  port Time
  port Tlm

}

passive component C {

  telemetry port tlmOut
  time get port timeGetOut

  @ An array of 3 F64 values
  array F64x3 = [3] F64

  @ Telemetry channel 0
  telemetry Channel0: U32 id 0x00

  @ Telemetry channel 1
  telemetry Channel1: U32 \
    id 0x01 \
    update on change

  @ Telemetry channel 2
  telemetry Channel2: F64 \
    id 0x02 \
    format "{.3f}"

  @ Telemetry channel 3
  telemetry Channel3: F64x3 \
    id 0x03 \
    low { yellow -1, orange -2, red -3 } \
    high { yellow 1, orange 2, red 3 }

}
