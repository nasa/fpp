constant a = 0

module M {

  constant a = 0
  enum E { X = a, Y = X }

  module N {

    constant a = 0
    enum E { X = M.a, Y = a, Z = X }

  }

}

constant b = a
constant c = M.a
constant d = M.N.a
