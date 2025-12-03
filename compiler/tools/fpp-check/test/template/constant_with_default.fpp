array StringArray = [3] string size 10

struct ParamTy {
    field1: U32
    field2: string size 10
    field3: StringArray
} default {
    field1 = 1,
    field2 = "field 2"
    field3 = [ "field3[0]", "field3[1]", "field3[2]" ]
}

template T(
    constant p: ParamTy
) {
    constant newField = p.field2
    constant newField2 = p.field3[1]
}

expand T(constant {})
