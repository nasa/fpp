@ ----------------------------------------------------------------------
@ Data product ports (async receive)
@ ----------------------------------------------------------------------
interface DataProductAsyncReceive {
    @ Data product request port
    product request port productRequestOut

    @ Data product receive port
    async product recv port productRecvIn

    @ Data product send port
    product send port productSendOut
}
