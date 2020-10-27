enum E { X, Y }
struct S { x: U32, e: E }
constant s = { x = 1, e = E.X }
array A = [3] S default [ s, s, s ]

passive component C {
  enum E { X, Y }
  struct S { x: U32, e: E }
  constant s = { x = 1, e = E.X }
  array A = [3] S default [ s, s, s ]
}

module M {
  enum E { X, Y }
  struct S { x: U32, e: E }
  constant s = { x = 1, e = E.X }
  array A = [3] S default [ s, s, s ]
}
