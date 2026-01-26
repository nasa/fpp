template T(constant c: U32) {
    constant k1 = c
    constant k2 = k1 + c
    constant k3 = k2 + c
}

expand T(constant 10)
