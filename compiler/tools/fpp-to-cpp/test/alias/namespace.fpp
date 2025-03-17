@ A simple type alias that supports C codegen
type SimpleCType = U32

@ A simple type alias that references another simple type alias
@ Tests if we can handle C header file include chaining
type SimpleCType2 = SimpleCType

module M {
    module M2 {
        @ Type alias in a nested namespace that references a basic type
        type NamespacedAliasType = SimpleCType2
    }

    @ Type within a namespace that references another namespaces type
    type NamespacedAliasType2 = M2.NamespacedAliasType
}

struct Namespace {
    A: SimpleCType,
    B: SimpleCType2,
    C: M.M2.NamespacedAliasType,
    D: M.NamespacedAliasType2
}
