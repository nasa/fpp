# The argument has the wrong type for a nested member
module template M(constant s: S) {
  constant c = s.inner.a
}

expand M(constant { inner = { a = "abc" } })

struct S { inner: Inner }

struct Inner { a: U32 } default { a = 3 }
