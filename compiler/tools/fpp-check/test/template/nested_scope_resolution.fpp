module M {

  constant n = 0

  module template T {
    module Inner {
      array A = [n] U32
    }
  }

}

module N {
  constant n = 3

  expand M.T
}
