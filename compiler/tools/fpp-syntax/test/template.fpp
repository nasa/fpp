module template Tmpl(
    @ Constant template parameter
    constant c: SomeType @< Constant parameter

    @ Type template parameter
    type TypeParam @< Type parameter

    @ Interface instance template parameter
    interface inst: InterfaceDef @< Instance parameter
) {
    array Arr = [3] TypeParam

    topology Top {
        instance inst
    }
}

@ Expansion specifier
expand Tmpl(constant 10, type Type, interface instance1)
@< Expansion specifier