# ----------------------------------------------------------------------
# Data product ports (guarded receive)
# ----------------------------------------------------------------------
interface DataProductGuardedReceive {
    @ Data product request port
    product request port productRequestOut

    @ Data product receive port
    guarded product recv port productRecvIn

    @ Data product send port
    product send port productSendOut
}