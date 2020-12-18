passive component Telemetry {

  telemetry port tlmOut

  time get port timeGetOut

  @ Channel C1
  telemetry C1: U32
  telemetry C2: U32 format "{}"
  telemetry C3: U32 update on change
  telemetry C4: string
  telemetry C5: I32 \
    low { red -3, orange -2, yellow -1 } \
    high { red 3, orange 2, yellow 1 }

}
