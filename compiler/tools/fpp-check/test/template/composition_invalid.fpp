module template T1(
    constant p: U32
) {
    constant c = p
}

module template T2(
    constant p: U32
) {
    expand T1(constant p)
}
