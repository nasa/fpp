@ Constant a
constant a = 0

@ Constant b
constant b = 1.0

@ Constant c
constant c = true

@ Constant d
constant d = "abc"

enum E { X = 3 }

@ Constant e
constant e = E.X

module M {

  @ Constant a
  constant a = 0

  @ Constant b
  constant b = 1.0

  @ Constant c
  constant c = true

  @ Constant d
  constant d = "abc"

  enum E { X = 3 }

  @ Constant e
  constant e = E.X

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

  enum E { X = 3 }

  @ Constant e
  constant e = E.X

}
