port P

passive component C {
    sync input port pIn: P
}

instance c: C base id 0x100

module template T(constant p: U32) {
    constant k = p
}

expand T(instance c)
