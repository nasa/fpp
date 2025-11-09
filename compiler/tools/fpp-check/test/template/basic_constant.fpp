template T(constant p: U32) {
    constant cOut = p + 2
}

expand T(10)

constant cOut2 = cOut + 2
