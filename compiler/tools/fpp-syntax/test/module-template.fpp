module template Tmpl(
    @ Constant template parameter
    constant c: SomeType @< Constant parameter
    @ Type template parameter
    type TypeParam @< Type parameter
    @ Interface instance template parameter
    instance inst: InterfaceDef @< Instance parameter
) {

    @ Array
    array Arr = [3] TypeParam

    @ Topology
    topology Top {
        instance inst
    }

    @ State machine
    state machine M {
      state S
      initial enter S
    }

}

module Unexpanded {

  @ Unexpanded expansion specifier
  expand Tmpl(constant 10, type Type, instance .instance1)
  @< Unexpanded expansion specifier

}

module Expanded {

  @ Expanded expansion specifier
  expand Tmpl(constant 10, type Type, instance .instance1) { constant a = 0 }
  @< Expanded expansion specifier

}
