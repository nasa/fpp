module Fw {
  port Tlm
  port Time
}

port P

passive component C {
  sync input port pIn: P
  output port pOut: P
  telemetry port tlmOut
  time get port timeGetOut
  telemetry T: U32
}

instance c1: C base id 0x100
instance c2: C base id 0x200

@ A topology with telemetry packets
topology TelemetryPackets {

  instance c1
  instance c2

  telemetry packets P {

    packet P1 id 0 group 0 {
      c1.T
    }

  } omit {
    c2.T
  }

}
