module template T(constant n: U32) {

  @ Constant computed from a template parameter
  constant scaled = n * 2

}

module M {
  expand T(constant 10)
}
