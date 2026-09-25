module template T {
    module Inner {
        constant a = 1
    }
}

module M {
    module Inner {
        constant b = 2
    }

    expand T
}

constant c = M.Inner.a + M.Inner.b
