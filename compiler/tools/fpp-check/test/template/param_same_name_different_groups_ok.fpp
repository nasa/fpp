module template T(constant x: U32, type x) {
    array A = [x] x default [1, 2, 3]
}

expand T(constant 3, type U32)
