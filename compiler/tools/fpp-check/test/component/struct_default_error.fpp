passive component C {
  array A = [3] U32
  enum E { X }
  struct S { x: U32, e: E, a: A } \
    default { x = 0, e = E.X, a = [ 1, 2, 3 ], b = 5 }
}
