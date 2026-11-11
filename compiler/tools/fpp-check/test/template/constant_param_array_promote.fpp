array B = [2] U32
struct S { b: B }

module template M(constant b: B, constant s: S) {

  @ An array type whose default value promotes a scalar to an array of B
  array C = [2] B default [ 1, b ]

  @ An array value that promotes a scalar to an array of B
  constant c = [ 1, b ]

  @ An array value whose elements are array values
  constant d = [ c, c ]

  @ A subscript of a promoted array value
  constant e = c[0][0]

  @ An array value whose elements are subscripts
  constant f = [ c[1], c[1] ]

  @ An array value that promotes a scalar to an array of a struct member
  constant g = [ 1, s.b ]

}

expand M(constant [2, 3], constant { b = [4, 5] })
