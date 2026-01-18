template T() {
    include "../../../../../../tools/fpp-syntax/test/syntax.fpp"
}

module M1 {
    expand T()
}

module M2 {
    expand T()
}
