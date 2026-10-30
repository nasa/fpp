type FwSizeStoreType = U16

constant FW_LOG_BUFFER_MAX_SIZE = 6

module Fw {

  port Log
  port LogText
  port Time

}

passive component C {

  event port eventOut
  text event port textEventOut
  time get port timeGetOut

  @ 4 bytes for x, plus at least 2 bytes for s
  event E(x: U32, s: string size 100) \
    severity activity low \
    format "{} {}"

}
