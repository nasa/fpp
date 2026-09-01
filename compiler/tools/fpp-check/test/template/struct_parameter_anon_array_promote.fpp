array A = [1] U32
array B = [2] U32

struct S { a: A, b: B }

module template M(constant b: B) {
  constant c = [ 1, b ]
}

expand M(constant [2, 3])
