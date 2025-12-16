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
