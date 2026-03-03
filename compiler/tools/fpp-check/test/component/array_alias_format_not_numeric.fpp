type FwSizeStoreType = U16
constant FW_FIXED_LENGTH_STRING_SIZE = 256

struct S { x: U32, y: string }
type T = S
array A = [3] T format "{.3f}"
