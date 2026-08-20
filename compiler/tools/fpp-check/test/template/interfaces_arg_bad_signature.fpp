port P

interface I {
    sync input port pIn: P
}

# A component that has a port named pIn, but with the wrong direction,
# so it does not conform to I
passive component C {
    output port pIn: P
}

instance c: C base id 0x100

module template T(interface i: I) {
    topology Top {
        instance i
    }
}

module M {
    # c.pIn does not match I.pIn, so it does not implement I
    expand T(interface c)
}
