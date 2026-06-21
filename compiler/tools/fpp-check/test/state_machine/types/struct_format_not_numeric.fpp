type FwSizeStoreType = U16
constant FW_FIXED_LENGTH_STRING_SIZE = 256

state machine SM {
  struct S1 { x: U32, y: string }
  struct S2 { x: S1 format "{.3f}" }
}
