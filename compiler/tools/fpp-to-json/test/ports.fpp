port P

port P1() # Zero parameters; equivalent to port P1
port P2(a: U32) # One parameter
port P3(a: I32, b: F32, c: string) # Three parameters

enum E { A, B }
type T
port P4(e: E, t: T, ref c: T) -> T

enum Status { SUCCEED, FAIL }
@ Pre annotation for port P5
port P5(ref result: U32) -> Status


module Fw {
    port DpResponse
}

module M {
  port P

  @ A component for generating periodic signals
  queued component Q {

    @ Data product receive port
    async product recv port productRecvIn

  }

}

instance i: M.Q base id 0x2100 \
    queue size 10


module M {
    topology T {
        instance i
    }
}
