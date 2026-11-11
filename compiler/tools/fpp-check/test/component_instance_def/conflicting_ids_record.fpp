module Fw {
  port DpRequest
  port DpResponse
  port DpSend
  port Time
}

passive component C {
  product request port productRequestOut
  sync product recv port productRecvIn
  product send port productSendOut
  time get port timeGetOut
  product container Container id 0
  product record R0: U32 id 0
  product record R1: U32 id 0x100
}

instance c1: C base id 0x100
instance c2: C base id 0x200
