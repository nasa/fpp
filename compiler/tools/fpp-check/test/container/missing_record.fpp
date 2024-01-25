module Fw {

  port DpGet
  port DpSend
  port Time

}

passive component C {

  product get port productGetOut
  product send port productSendOut
  time get port timeGetOut

  @ A container
  product container Container

}
