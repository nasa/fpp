module template T(constant n: U32) {
  array Duplicate = [n] U32
}

module M1 {
  expand T(constant 1)
}

module M2 {
  expand T(constant 2)
}
