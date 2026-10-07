# The expansion appears before the struct definitions, so evaluating the
# argument must evaluate and finalize the nested struct defaults on demand
module template M(constant s: S) {
  array A = [s.inner.a] U32 default [1, 2, 3]
}

expand M(constant {})

struct S { inner: Inner }

struct Inner { a: U32 } default { a = 3 }
