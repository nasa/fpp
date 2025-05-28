port P()

interface I {
    async input port P: P
}

interface J {
    import I
}

interface K {
    import J
}

passive component C {
    import K
}
