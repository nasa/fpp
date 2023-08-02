module Sensors {

  @ A port for calibration input
  port Calibration(cal: F32)

  @ A component for sensing engine temperature
  queued component EngineTemp {

    @ Schedule input port
    sync input port schedIn: Svc.Sched

    @ Calibration input
    async input port calibrationIn: Calibration

    @ Telemetry port
    telemetry port tlmOut

    @ Time get port
    time get port timeGetOut

    @ Impulse engine temperature
    telemetry ImpulseTemp: F32

    @ Warp core temperature
    telemetry WarpTemp: F32

  }

}

module FSW {

  @ Engine temperature sensor
  instance engineTemp: Sensors.EngineTemp base id 0x100 \
    queue size 10

}

module Svc {
  port Sched
}

module Fw {
  port Tlm
  port Time
}
