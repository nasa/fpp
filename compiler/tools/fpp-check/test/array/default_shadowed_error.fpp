enum E { X, Y }
struct S { x: U32, e: E }
constant s = { x = 1, e = E.X }

module M {
  struct S {}
  array A = [3] S default [ s, s, s ]
}
