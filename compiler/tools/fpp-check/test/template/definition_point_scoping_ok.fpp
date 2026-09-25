module M {
    constant a = 3

    module template T {
        constant b = a
    }
}

module N {
    constant a = 0

    expand M.T
}

array Discriminator = [N.b] U32 default [1, 2, 3]
