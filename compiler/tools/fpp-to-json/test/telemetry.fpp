@ Component for illustrating telemetry channel limits
passive component TlmLimits {

  # ----------------------------------------------------------------------
  # Ports
  # ----------------------------------------------------------------------

  @ Telemetry port
  telemetry port tlmOut

  @ Time get port
  time get port timeGetOut

  # ----------------------------------------------------------------------
  # Telemetry
  # ----------------------------------------------------------------------

  @ Telemetry channel 1
  telemetry Channel1: U32 \
    low { red 0, orange 1, yellow 2 }

  @ Telemetry channel 2
  telemetry Channel2: F64 id 0x10 \
    update on change \
    format "{.3f}" \
    low { red -3, orange -2, yellow -1 } \
    high { red 3, orange 2, yellow 1 }

  @ Telemetry channel 3
  telemetry Channel3: F64 \
    update always \
    format "{e}" \
    high { red 3, orange 2, yellow 1 }

}

module Fw {
  port Tlm
  port Time
}
