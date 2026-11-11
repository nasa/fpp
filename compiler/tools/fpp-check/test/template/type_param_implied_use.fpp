# A string type in a template body implies a use of FwSizeStoreType.
# A bound template parameter may not satisfy that implied use.
# The explicit size is load-bearing: a default-size string also implies a use
# of the constant FW_FIXED_LENGTH_STRING_SIZE, whose error masks this one.

module template T(type FwSizeStoreType) {
    array A = [1] string size 256
}

expand T(type U32)
