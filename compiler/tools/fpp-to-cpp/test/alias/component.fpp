type T = U32
type StringA = string size 60

module Fw {
  port Tlm
  port Time
  port Log
  port DpGet
  port DpSend
}

port AliasPort(
  a1: T,
  a2: StringA
) -> StringA

passive component C {
  array A = [3] U32
  type T = A
  type T2 = string size 30

  telemetry port tlmOut
  event port eventOut
  time get port timeGet
  product get port productGet
  product send port productSend

  # Strings has special handling when serialized
  # We should validate that the underyling type is
  # actually selected

  telemetry E1: T
  telemetry E2: T2
  telemetry E3: string size 30

  sync input port P1: AliasPort
  sync input port P2: [3] AliasPort

  product record R1: T2 id 0x11
  product container C1
}
