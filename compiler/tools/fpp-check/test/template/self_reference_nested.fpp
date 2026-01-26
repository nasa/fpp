template Outer(constant c1: U32) {
    template Inner(constant c2: U32) {
        constant k1 = c2 * 2
    }

    module M1 {
        expand Inner(constant 10)
        constant k2 = k1 * 3
    }

    module M2 {
        expand Inner(constant 20)
        constant k2 = k1 * 3
    }

    expand Inner(constant 30)

    constant j1 = M1.k1 + M2.k2
    constant j2 = k1 + M1.k1 + M2.k1
}

expand Outer(constant 10)
