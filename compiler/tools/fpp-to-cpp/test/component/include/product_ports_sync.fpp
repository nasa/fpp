@ ----------------------------------------------------------------------
@ Data product ports (sync receive)
@ ----------------------------------------------------------------------
interface DataProductSyncReceive {
    @ Data product request port
    product request port productRequestOut

    @ Data product receive port
    sync product recv port productRecvIn

    @ Data product send port
    product send port productSendOut
}
