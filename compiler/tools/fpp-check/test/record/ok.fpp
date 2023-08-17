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

  @ A record with no ID
  product record NoId: U32

  @ A record with ID
  product record Id: U32 id 0x100

  @ A raw record
  product record Raw: raw

}