struct S { a: U32, b: U32 } default { a = 1, b = 2 }

module template M(constant s: S) {
  array A = [s.a] U32 default [1, 2, 3, 4, 5]
  array B = [s.b + 3] U32 default [1, 2, 3]
}

expand M(constant { a = 5 })
