array A = [1] U32
array B = [2] U32

struct S { a: A, b: B }

constant b1 = [2, 3]
constant c1 = [ 1, b1 ]

module template M(constant b: B) {
  constant c = [ 1, b ]
}

expand M(constant [2, 3])
