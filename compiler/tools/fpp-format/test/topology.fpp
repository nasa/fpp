@ Topology definition
topology T implements I1,I2{
    instance i1
    instance i2

    import T2

    port p2 = i1.p2

    connections Conn { i1.pOut -> i2.pIn }
}
