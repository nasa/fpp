constant FW_LOG_BUFFER_MAX_SIZE = 8

module Fw {

  port Log
  port LogText
  port Time

}

passive component C {

  event port eventOut
  text event port textEventOut
  time get port timeGetOut

  event E(x: U32, y: U64) \
    severity activity low \
    format "{} {}"

}
