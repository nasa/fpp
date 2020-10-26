constant a = 0
constant b = a
constant c = M.a
constant d = M.N.a

constant hex1 = 0xFF
constant hex2 = 0xff
constant hex3 = 0XFF
constant hex4 = 0Xff

module M {

  constant a = 0
  constant e = E.X
  enum E { X = a, Y = X + 1}

  module N {

    constant a = 0
    constant e = E.X
    enum E { X = M.a, Y = a + 1, Z = Y + 2 }

  }

}

constant e = C.b

passive component C {

  constant a = 1
  constant b = a

}
