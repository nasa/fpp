port P

interface A_I {
    async input port aIn: [2] P
    output port bOut: [2] P
}

interface B_I {
    output port aOut: [2] P
    async input port bIn: [2] P
}

active component A1 {
    import A_I

    async input port aIn2: [2] P
}

active component A2 {
    import A_I
}

active component B {
    import B_I
}

# Two instances of the same component
instance a1: A1 base id 10 queue size 10 stack size 1024
instance a2: A2 base id 20 queue size 10 stack size 1024

instance b: B base id 100 queue size 10 stack size 1024

template T(interface i: A_I, constant idx: U32) {
    topology P {
        instance i
        instance b

        connections C {
            i.bOut[idx] -> b.bIn[idx]

            # Input port exists on `a1` and `a2` put not in `A_I`
            b.aOut[idx] -> i.aIn2[idx]
        }
    }
}

# Instantiate two topologies that point to separate connections of b
module M1 {
    expand T(interface a1, constant 0)
}

module M2 {
    module M {
        # expand T(interface a2, constant 1)
    }
}
