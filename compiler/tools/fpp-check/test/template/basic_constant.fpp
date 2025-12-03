template T(constant p: U32) {
    constant cOut = p + 2
}

expand T(constant 10)

constant cOut2 = cOut + 2
