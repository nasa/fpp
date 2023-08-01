@ This is module M1
module M1 {
  constant a = 1
  constant b = a # OK: refers to M1.a
  constant c = M1.b
}

constant a = M1.a

module M2 {
  constant a = 0
}
module M2 {
  constant b = 1
}

module A {
  module B {
    constant c = 0
  }
}
constant c = A.B.c
