module template T(constant x: U32) {
    constant x = 0
    constant c = x
}

expand T(constant 3)
