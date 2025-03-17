type FwOpcodeType

@ An alias of a built-in type
type BuiltInType = FwOpcodeType

module M {
    type NamespacedBuiltin1 = FwOpcodeType
    type NamespacedBuiltin2 = BuiltInType
}

struct Builtin {
    A: BuiltInType,
    B: M.NamespacedBuiltin1,
    C: M.NamespacedBuiltin2,
}
