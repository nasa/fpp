type FwSizeStoreType = U16

# A vector of vectors, and an array of vectors
vector Inner = [size 2] U8
vector Outer = [size 3] Inner default [ [ 1, 2 ], [ 3, 4 ] ]
array A = [2] Inner default [ [ 1 ], [ 1 ] ]
