type stringAlias = string
type f32Alias = F32

constant C = 1.0

array A = [2] f32Alias

enum E {
    X
    Y
    Z
}

enum E2 {
    A
    B
    C
}

dictionary constant C2 = 1
dictionary constant C3 = E2.A

dictionary struct S {
    X: A
    Y: stringAlias
}

dictionary array A2 = [3] U32

topology DictionaryDefs {

}
