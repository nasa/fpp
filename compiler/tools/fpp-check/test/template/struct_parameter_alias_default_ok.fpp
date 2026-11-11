# The parameter type is an alias of a struct defined after the expansion
module template M(constant s: T) {
  array A = [s.inner.a] U32 default [1, 2, 3]
}

expand M(constant {})

type T = S

struct S { inner: Inner }

struct Inner { a: U32 } default { a = 3 }
