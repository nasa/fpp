type FwSizeStoreType = U16
constant FW_FIXED_LENGTH_STRING_SIZE = 256

state machine SM {
  struct S { x: U32, y: string }
  type T = S
  array A = [3] T format "{.3f}"
}
