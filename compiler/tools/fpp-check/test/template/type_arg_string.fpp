# A string type name used as a template type argument
# implies a use of FwSizeStoreType

module template T(type U) {
    constant c = 0
}

expand T(type string size 256)
