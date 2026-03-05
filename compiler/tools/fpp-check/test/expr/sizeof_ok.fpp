constant c1 = sizeof(U8)
constant c2 = sizeof(U16)
constant c3 = sizeof(U32)
constant c4 = sizeof(U64)
constant c5 = sizeof(I8)
constant c6 = sizeof(I16)
constant c7 = sizeof(I32)
constant c8 = sizeof(I64)
constant c9 = sizeof(F32)
constant c10 = sizeof(F64)
constant c11 = sizeof(bool)
constant c12 = sizeof(string size 10)
constant c13 = sizeof(A)
constant c14 = sizeof(A2)
constant c15 = sizeof(A3)
constant c16 = sizeof(E)
constant c17 = sizeof(E2)
constant c18 = sizeof(S)
constant c19 = sizeof(S2)
constant c20 = sizeof(T)
constant c21 = sizeof(T2)
constant c22 = sizeof(T3)
constant c23 = 4 * sizeof(U32)
constant c24 = 2 + (sizeof(U32) + sizeof(T3))
constant c25 = sizeof(E) + sizeof(A) + c18

enum E {
    X = 0
    Y = 1
}

struct S {
    W: F32
    X: string size 20
    Y: bool
    Z: [2] U64
} default { X = "hello world", Y = true, Z = 10 }

struct S2 {
    X: E2
    Y: [2] A
    Z: S
}

enum E2: U32 {
    X
    Y
    Z
} default Z

array A = [1] A3
array A2 = [1] S
array A3 = [3] string

type T = U32
type T2 = A3
type T3 = S
type T4 = string size sizeof(string size (sizeof(A) + sizeof(E) + sizeof(S) + sizeof(string size 10.6)))

type FwSizeStoreType = U16
constant FW_FIXED_LENGTH_STRING_SIZE = 256
