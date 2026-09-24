module M {

  @ Enum from a template, values computed from a constant template parameter
  enum E: U32 {
    @ First value
    A = 10
    @ Second value
    B = 10 + 1
  }

  @ State machine from a template
  state machine SM {

    @ Signal carrying the enum defined by the same template
    signal s: E

    @ Action
    action a

    @ Guard
    guard g

    initial enter S

    @ The initial state
    state S {
      on s if g do { a } enter Tt
    }

    @ The final state
    state Tt {
      entry do { a }
    }

  }

}
