type FwSizeStoreType = U16
constant FW_FIXED_LENGTH_STRING_SIZE = 256

struct S1 { x: U32, y: string }
type T = S1
struct S2 { x: T format "{.3f}" }
