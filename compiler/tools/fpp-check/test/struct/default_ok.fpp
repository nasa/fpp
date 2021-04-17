array A = [3] U32
enum E { X, Y }
struct S { x: U32, e: [3] E, a: A } \
  default { x = 5, e = E.Y }

passive component C {
  array A = [3] U32
  enum E { X, Y }
  struct S { x: U32, e: [3] E, a: A } \
    default { x = 5, e = E.Y }
}

module M {
  array A = [3] U32
  enum E { X, Y }
  struct S { x: U32, e: [3] E, a: A } \
    default { x = 5, e = E.Y }
}

