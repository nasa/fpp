module M {

  constant n = 3

  module template T {

    module Inner {
      array IA = [n] U32 default [1, 2, 3]
    }

    passive component C {
      array CA = [n] U32 default [1, 2, 3]
    }

    array A = [n] U32 default [1, 2, 3]

    struct S {
      x: [n] U32
    }

  }

}

module N {
  constant n = 0

  expand M.T
}
