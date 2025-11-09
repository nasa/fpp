template T(constant p: U32) {
    constant cOut1 = p + 2
    constant cOut2 = p + 2
}

expand T(10)

constant cOut2 = cOut1 + 2
