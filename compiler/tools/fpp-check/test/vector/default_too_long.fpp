type FwSizeStoreType = U16

# The length of the default value must not exceed the maximum size
vector V = [size 2] U8 default [ 1, 2, 3 ]
