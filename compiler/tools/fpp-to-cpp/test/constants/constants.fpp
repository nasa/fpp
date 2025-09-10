@ Constant a
constant a = 0

@ Constant b
constant b = 1.0

@ Constant c
constant c = true

@ Constant d
constant d = "abc"

enum E1 { X = 3 }

@ Constant e
constant e = E1.X

@ Constant f
constant f = [ 1, 2, 3 ]

module M {

  @ Constant a
  constant a = 0

  @ Constant b
  constant b = 1.0

  @ Constant c
  constant c = true

  @ Constant d
  constant d = "abc"

  enum E2 { X = 3 }

  @ Constant e
  constant e = E2.X

}

passive component C {

  @ Constant a
  constant a = 0

  @ Constant b
  constant b = 1.0

  @ Constant c
  constant c = true

  @ Constant d
  constant d = "abc"

  enum E3 { X = 3 }

  @ Constant e
  constant e = E3.X

  @ Constant f
  constant g = f[1]

}
