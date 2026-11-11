struct LibElt { x: U32 }

module template TLib(constant n: U32) {
  array LibArr = [n] U32
}

module Lib {
  expand TLib(constant 2)
}
