type FwSizeStoreType = U16
constant FW_FIXED_LENGTH_STRING_SIZE = 256

type TStructCycle = SSCycle

struct SSCycle {
    F1: string
    F2: U32
    F3: bool
    F4: TStructCycle
}
