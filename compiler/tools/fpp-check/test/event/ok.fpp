module Fw {

  port Log
  port LogText

  port Time

}

passive component C {

  event port eventOut
  text event port textEventOut

  time get port timeGetOut

  @ An array of 3 F64 values
  array F64x3 = [3] F64

  @ An enumeration of cases
  enum Case { A, B, C }

  @ Event 0
  event Event0 \
    severity activity low \
    id 0x00 \
    format "Event 0 occurred"

  @ Event 1
  @ Sample output: "Event 1 occurred with argument 42"
  event Event1(
    arg1: U32 @< Argument 1
  ) \
    severity activity high \
    id 0x01 \
    format "Event 1 occurred with argument {}"

  @ Event 2
  @ Sample output: "Saw value [ 0.001, 0.002, 0.003 ] for case A"
  event Event2(
    case: Case @< The case
    value: F64x3 @< The value
  ) \
    severity warning low \
    id 0x02 \
    format "Saw value {} for case {}" \
    throttle 10

}
