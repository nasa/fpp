module template T(constant n: U32) {

  @ Constant computed from a template parameter
  constant scaled = n * 2

}

module M1 {
  expand T(constant 1)
}

module M2 {
  expand T(constant 2)
}
