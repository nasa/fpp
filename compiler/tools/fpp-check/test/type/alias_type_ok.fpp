enum E1T {
    E1 = 0
    E2 = 1
    E3 = 3
}

struct SS {
    F1: string
    F2: U32
    F3: bool
}

type TU = U32
type TS = string size 20
type TE = E1T
type TB = bool
type TStruct = SS

constant B = false

array A1 = [2] TB default [B, B]
array A2 = [2] TE default [E1T.E1, E1T.E2]
array A3 = [2] TU default [1, 2]
array A4 = [2] TS default ["1", "2"]
array A5 = [2] TStruct default [{
    F1 = "test",
    F2 = 1,
    F3 = false,
}, {
    F1 = "test2",
    F2 = 2,
    F3 = true,
}]
