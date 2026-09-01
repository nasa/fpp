port P

interface I {
    sync input port pIn: P
    output port pOut: P
}

passive component C {
    sync input port pIn: P
    output port pOut: P
    output port extra: P
}

instance c: C base id 0x100

module template T(interface i: I) {
    topology Top {
        instance i
    }
}

module M {
    # c provides a superset of I, so it conforms
    expand T(interface c)
}
