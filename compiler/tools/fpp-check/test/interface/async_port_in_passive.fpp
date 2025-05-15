port P()

interface I {
    async input port P: P
}

passive component C {
    import I
}
