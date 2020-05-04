type T

array A = [3] T
struct S { a: T, b: A }
array B = [3] S



module M {

  type T
  array A = [3] T
  struct S { a: T, b: A }
  array B = [3] S

}

module N {

  array A = [3] M.T
  struct S { a: M.T, b: M.A }
  array B = [3] S

}
