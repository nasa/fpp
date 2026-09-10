module template TMain(constant n: U32) {
  array MainArr = [n] LibElt
}

module Main {
  expand TMain(constant 3)
}
