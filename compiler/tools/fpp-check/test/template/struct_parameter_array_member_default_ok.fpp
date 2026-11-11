# The parameter type has an array member whose element type is a struct
# defined after the expansion
module template M(constant s: S) {
  array A = [s.elts[1].a] U32 default [1, 2, 3]
}

expand M(constant {})

struct S { elts: Elts }

array Elts = [2] Inner

struct Inner { a: U32 } default { a = 3 }
