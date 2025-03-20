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

module M {
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
    event Event1 severity activity low \
      id 0x10 \
      format "Event 1 occurred"

    @ Event 2
    event Event2(
      $port: I32 @< Port arg
    ) \
      severity command \
      id 2 \
      format "Port {}"

    @ Event 3
    @ Event with 3 args
    event Event3(
      arg1: I32 @< Arg1
      arg2: F32 @< Arg2
      arg3: U8 @< Arg3
    ) \
      severity activity high \
      id 9 \
      format "Event3 args: I32: {}, F32: {f}, U8: {}"

    @ Exceeded the number of commands that can be simultaneously executed
    event TooManyCommands(
        Opcode: U32 @< The opcode that overflowed the list
    ) \
      severity warning high \
      id 6 \
      format "Too many outstanding commands. opcode=0x{x}"

  }
}

module Fw {
  port Log
  port LogText
  port Time
}
