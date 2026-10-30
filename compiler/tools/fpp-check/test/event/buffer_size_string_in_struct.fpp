type FwSizeStoreType = U16

constant FW_LOG_BUFFER_MAX_SIZE = 9

module Fw {

  port Log
  port LogText
  port Time

}

@ At least 2 bytes for s, plus 8 bytes for x
struct S { s: string size 100, x: U64 }

passive component C {

  event port eventOut
  text event port textEventOut
  time get port timeGetOut

  event E(s: S) \
    severity activity low \
    format "{}"

}
