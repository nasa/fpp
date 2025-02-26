array A = [3] U32

struct S {
  x: U32
  y: F32
}

passive component C { 

  telemetry T_string: string
  telemetry T_u32: U32
  telemetry T_f32: F32
  telemetry T_bool: bool
  telemetry T_A: A
  telemetry T_S: S

  time get port timeGetOut

  telemetry port tlmOut

}

module M {

  instance c1: C base id 0x100
  instance c2: C base id 0x200
  
  topology TlmPackets { 

    telemetry packets P1 {

    }

    telemetry packets P2 {

      packet P1 group 0 {

      }

    }

  }

}
