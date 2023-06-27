@ Component for illustrating event identifiers
passive component EventIdentifiers {

  # ----------------------------------------------------------------------
  # Ports
  # ----------------------------------------------------------------------

  @ Event port
  event port eventOut

  @ Text event port
  text event port textEventOut

  @ Time get port
  time get port timeGetOut

  # ----------------------------------------------------------------------
  # Events
  # ----------------------------------------------------------------------

  @ Event 1
  @ Its identifier is 0x00
  event Event1 severity activity low \
    id 0x10 \
    format "Event 1 occurred"

  @ Event 2
  @ Its identifier is 0x10
  event Event2(
    count: U32 @< The count
  ) \
    severity activity high \
    id 0x11 \
    format "The count is {}"

  @ Event 3
  @ Its identifier is 0x11
  event Event3 severity activity high \
    format "Event 3 occurred"

}

@ Component for illustrating event throttling
passive component EventThrottling {

  # ----------------------------------------------------------------------
  # Ports
  # ----------------------------------------------------------------------

  @ Event port
  event port eventOut

  @ Text event port
  text event port textEventOut

  @ Time get port
  time get port timeGetOut

  # ----------------------------------------------------------------------
  # Events
  # ----------------------------------------------------------------------

  @ Event 1
  event Event1 severity warning high \
    format "Event 1 occurred" \
    throttle 10

}

module Fw {
  port Log
  port LogText
  port Time
}
