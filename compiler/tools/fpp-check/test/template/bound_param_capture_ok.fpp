module template T(constant n: U32) {
  module Inner {
    array A = [n] U32 default [1, 2, 3]
  }
}

module M {
  module Inner {
    constant n = 0
  }

  expand T(constant 3)
}
