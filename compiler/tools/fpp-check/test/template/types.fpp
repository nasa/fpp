module Fw {

  port Time
  port Tlm

}

template T(type P) {
    port POut(p: P) -> P

    passive component C {
        telemetry port tlmOut
        time get port timeGetOut

        @ Telemetry channel 0
        telemetry Channel0: P id 0x00
    }
}

struct S {
    member: U32
}

expand T(S)
