type T

array A = [3] T
struct S { a: T, b: A }

module M {

  type T
  array A = [3] T
  struct S { a: T, b: A }

}

module N {

  array A = [3] M.T
  struct S { a: M.T, b: M.A }

}
