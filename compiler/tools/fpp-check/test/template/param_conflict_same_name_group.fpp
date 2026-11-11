module template T(constant x: U32, constant x: I32) {
    constant c = x
}

expand T(constant 1, constant 2)
