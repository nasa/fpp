template T(constant p: U32) {
    constant i1 = p + 2
    constant i2 = i1 + 3
}

expand T(10)
