# Dictionary definitions (included in the dictionary)
dictionary type T = T2
dictionary array A = [3] U32
dictionary enum E { 
    A 
    B 
    C 
} default C2

dictionary struct S {
    X: string,
    Y: A2,
    Z: S2
}

dictionary constant C = E2.A

passive component P {

  dictionary constant C = 0

}

# Defintions that are uses of dictionary definitions (included in the dictionary)
type T2 = U32
array A2 = [3] U32
enum E2 { 
    A 
} default C

struct S2 { 
    X: T 
}
constant C2 = E.A

# Non dictionary definitions (not included in the dictionary)
type T3 = F32
array A3 = [3] string
enum E3 { 
    X
}

struct S3 { 
    X: string 
}

constant C3 = 1

topology DictionaryDefs {

}
