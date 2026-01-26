port P

interface A_I1 {
    async input port aIn: [2] P
    output port bOut: [2] P
}

interface A_I2 {
    async input port aIn2: [2] P
    output port bOut2: [2] P
}

interface B_I {
    output port aOut: [2] P
    async input port bIn: [2] P
}

active component A1 {
    import A_I1
    import A_I2

    async input port aIn3: [2] P
}

active component B {
    import B_I
}

# Two instances of the same component
instance a1: A1 base id 10 queue size 10 stack size 1024
instance b: B base id 100 queue size 10 stack size 1024

template T(interface i: A_I1, constant idx: U32) {
    template Inner(interface innerInstance: A_I2) {
        topology InnerTop {
            instance innerInstance
        }
    }

    expand Inner(interface a1)

    topology P {
        # Import `a1` using the `A_I2` interface
        instance InnerTop

        # Import `a1` using the `A_I1` interface
        instance i

        instance b

        connections C {
            # Connect to a port inside `A_I1`
            i.bOut[idx] -> b.bIn[idx]

            # Connect to a port inside `A_I2`
            b.aOut[idx] -> i.aIn2[idx]
        }
    }
}

# Instantiate two topologies that point to separate connections of b
module M1 {
    expand T(interface a1, constant 0)
}

topology Top {
    instance M1.P
}
