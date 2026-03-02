type FwSizeStoreType = U16
constant FW_FIXED_LENGTH_STRING_SIZE = 256

module M {
  @ A port with a string return type
  port StringReturnType -> string
}
