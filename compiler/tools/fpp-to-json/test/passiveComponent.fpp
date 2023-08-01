module Sensors {

  @ A component for sensing engine temperature
  passive component EngineTemp {

    @ Schedule input port
    sync input port schedIn: Svc.Sched

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

  @ Engine temperature instance
  instance engineTemp: Sensors.EngineTemp base id 0x100

}

module Svc {
  port Sched
}

module Fw{
  port Tlm
  port Time
}
