port P()

interface I {
    async input port P: P
}

active component C {
    output port P: P
    import I
}
