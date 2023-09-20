module Fw {
  port DpResponse
  port DpRequest
  port DpSend
  port Time
}

passive component C {

  product record R: U32

  product request port productRequestOut

  product send port productSendOut

  time get port timeGetOut

}
