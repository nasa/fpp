module template T(constant p: U32, constant q: U32) {
    constant c = p + q
}

expand T(constant 1)
