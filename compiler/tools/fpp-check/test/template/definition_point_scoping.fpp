module M {
    constant a = 0

    module template T {
        constant b = a
    }
}

module N {
    constant a = 4

    expand M.T
}

array Discriminator = [N.b] U32
