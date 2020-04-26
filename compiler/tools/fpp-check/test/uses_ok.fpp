constant a = 0
constant b = a
constant c = M.a
constant d = M.N.a

module M {

  constant a = 0
  constant e = E.X
  enum E { X = a, Y = X }

  module N {

    constant a = 0
    constant e = E.X
    enum E { X = M.a, Y = a, Z = X }

  }

}
