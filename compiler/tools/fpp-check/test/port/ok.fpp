type FwSizeStoreType = U16
constant FW_FIXED_LENGTH_STRING_SIZE = 256

port P1
port P2(a: U32, b: string)
port P3(a: F32) -> F32

type Time
port Time1 -> Time
port Time2(ref t: Time)
