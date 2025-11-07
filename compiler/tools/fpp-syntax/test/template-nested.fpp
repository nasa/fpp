template T(constant p1: U32) {
    template D(constant p2: U32) {
        constant i1 = p2 + 1
    }

    expand D(p1 + 2)
}

expand T(10)
