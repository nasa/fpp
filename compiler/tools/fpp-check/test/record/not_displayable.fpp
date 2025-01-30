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

  type A
  product container Container
  product record R: A id 0x100

}
