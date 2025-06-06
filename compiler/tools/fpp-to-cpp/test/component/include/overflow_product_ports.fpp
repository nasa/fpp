# ----------------------------------------------------------------------
# Data product ports (async receive)
# ----------------------------------------------------------------------
@ Data product receive port with overflow hook
interface DataProductAsyncReceiveOverflow {
    async product recv port productRecvInHook hook
}
