module template T(constant p: U32) {
    constant c = p
}

expand T(constant 1, constant 2)
