type FwSizeStoreType = U16

# Implicit size-prefix type (FwSizeStoreType = U16): 2 + 3 * 1 = 5
vector V = [size 3] U8
# Explicit U16 size-prefix type: 2 + 3 * 2 = 8
vector W = [U16 size 3] U16

constant a = sizeof(V)
constant b = sizeof(W)
