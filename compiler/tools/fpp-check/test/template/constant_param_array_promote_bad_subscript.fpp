array B = [3] U32

# The size of B is not known when the constant expressions are first
# evaluated, so the subscript below is checked when they are finalized
module template M(constant b: B) {

  @ An array value that promotes a scalar to an array of B
  constant c = [ 1, b ]

  @ A subscript that is out of range for the size of B
  constant e = c[0][3]

}

expand M(constant [2, 3, 5])
