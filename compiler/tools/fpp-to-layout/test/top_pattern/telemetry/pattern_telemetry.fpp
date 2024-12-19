module Fw {
  type Time
  port Time($time: Fw.Time)
  port Tlm
}
module M {
  passive component Telemetry {
    sync input port tlmIn: Fw.Tlm
  }
  passive component C {
    time get port timeGetOut
    telemetry port tlmOut
  }
  instance $telemetry: Telemetry base id 0x100
  instance c1: C base id 0x200
  instance c2: C base id 0x300
  topology T {
    instance $telemetry
    instance c1
    instance c2
    telemetry connections instance $telemetry
  }
}
