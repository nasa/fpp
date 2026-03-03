array A = [1] U32
array B = [2] U32

struct S { a: A, b: B }

template M(constant s: S) {
  constant c = [ s.a[0], s.b[1] ]
}

expand M(constant { a = [1], b = [2, 3] })
