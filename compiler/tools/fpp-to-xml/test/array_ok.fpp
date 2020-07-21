module M {
array E = [5] U32 default 1 format "{.03f}"
}

module N {
array F = [3] M.E
}

array G = [2] string default ["string1", "string2"]

module L {
    array H = [4] G @< Array with array arg
}

type T
array AbsArray = [3] T