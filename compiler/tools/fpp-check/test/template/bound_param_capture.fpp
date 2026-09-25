module template T(constant n: U32) {
  module Inner {
    array A = [n] U32
  }
}

module M {
  module Inner {
    constant n = 3
  }

  expand T(constant 0)
}
