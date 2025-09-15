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
  constant b = 1.5

  @ Constant c
  constant c = true

  @ Constant d
  constant d = "abc"

  enum E2 { X = 3 }

  @ Constant e
  constant e = E2.X

  constant f = { a = b, b = { a = b + 1 }, c = [ 0, 1, 2 ] }
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
  constant f = M.f

  @ Constant g
  constant g = f.c[1]

  @ Constant h
  constant h = M.f.b

  @ Constant j
  constant j = M.f.b.a + 1

  @ Constant k
  constant k = { x = 1.2 + M.f.b.a }.x
}
