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

  @ A container
  product container Container

  @ A record with no ID
  product record NoId: U32

  @ A record with ID
  product record Id: U32 id 0x100

  @ An array record
  product record U32Array: U32 array

}
