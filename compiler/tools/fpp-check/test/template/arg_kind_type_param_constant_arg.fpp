module template T(type Ty) {
    array A = [1] Ty
}

expand T(constant 0)
