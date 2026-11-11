port P

passive component C {
    sync input port pIn: P
}

instance c: C base id 0x100

module template T(type Ty) {
    array A = [1] Ty
}

expand T(instance c)
