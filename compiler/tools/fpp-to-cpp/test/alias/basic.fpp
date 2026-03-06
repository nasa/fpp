type TU32 = U32
type TF32 = F32
type TString = string
type TStringSize = string size 2

struct Basic {
    A: TU32,
    B: TF32,
    C: TString,
    D: TStringSize
}

type FwSizeStoreType = U16
constant FW_FIXED_LENGTH_STRING_SIZE = 256
