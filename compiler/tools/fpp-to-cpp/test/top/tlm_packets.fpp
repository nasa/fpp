array A = [3] U32

struct S {
  x: U32
  y: F32
}

passive component C { 

  telemetry T1: string
  telemetry T2: U32
  telemetry T3: F32
  telemetry T4: bool
  telemetry T5: A
  telemetry T6: S

  time get port timeGetOut

  telemetry port tlmOut

}

module M {

  instance c1: C base id 0x100
  instance c2: C base id 0x200
  
  topology NoInstances { 

    telemetry packets P1 {

    }

    telemetry packets P2 {

      packet P1 group 0 {

      }

    }

  }

  topology OneInstance {

    instance c1

    telemetry packets P1 {

      packet P1 group 0 {
        c1.T1
        c1.T2
        c1.T3
      }

      packet P2 group 0 {
        c1.T4
        c1.T5
        c1.T6
      }

    }

    telemetry packets P2 {

      packet P1 group 0 {
        c1.T1
        c1.T2
        c1.T3
      }

    } omit {
      c1.T4
      c1.T5
      c1.T6
    }

    telemetry packets P3 {

    } omit {
      c1.T1
      c1.T2
      c1.T3
      c1.T4
      c1.T5
      c1.T6
    }

  }

}
