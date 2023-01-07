module Fw {

  port DpBufferRecv
  port DpBufferRequest
  port DpBufferSend

}

passive component C {

  product request port productRequestOut
  sync product recv port productRecvIn
  product send port productSendOut

  @ A record with no ID
  product record NoId: U32

  @ A record with ID
  product record Id: U32 id 0x100

}
