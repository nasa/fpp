module M {
array E = [5] U32 default 1 format "{.03f}"
}

module N {
array F = [3] M.E
}