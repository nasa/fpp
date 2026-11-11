module template T {
    constant c = 1
}

module template U() {
    constant d = 2
}

module M1 {
    expand T
}

module M2 {
    expand T()
}

module M3 {
    expand U
}

module M4 {
    expand U()
}

constant e = M1.c + M2.c + M3.d + M4.d
