port P

interface Input {
    async input port a: P
    output port b: P
}

interface Output {
    output port a: [2] P
    async input port b: [2] P
}

active component A {
    import Input
}

active component B {
    import Output
}

# Two instances of the same component
instance a1: A base id 10
instance a2: A base id 20

instance b: B base id 100

template T(interface i: Input, constant idx: U32) {
    topology P {
        instance i
        instance b

        connections C {
            i.a[idx] -> b.b[idx]
            b.a[idx] -> i.b[idx]
        }
    }
}

# Instantiate two topologies that point to separate connections of b
module M1 {
    expand T(interface a1, constant 0)
}

module M2 {
    module M {
        expand T(interface a2, constant 1)
    }
}
