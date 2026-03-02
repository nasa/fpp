type FwSizeStoreType = U16
constant FW_FIXED_LENGTH_STRING_SIZE = 256

@ A port with string parameters
port String(
  str80: string, @< A string of size 80
  ref str80Ref: string,
  str100: string size 100, @< A string of size 100
  ref str100Ref: string size 100
)
