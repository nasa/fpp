module Fw {

  port Tlm

}

type B

array A = [3] B

active component C {

  telemetry T: A

  telemetry port tlmOut

}
