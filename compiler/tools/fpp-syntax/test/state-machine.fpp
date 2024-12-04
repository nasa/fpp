@ State machine M
state machine M {

  @ Action a1
  action a1

  @ Action a2
  action a2

  @ Action a3
  action a3

  @ Action a4
  action a4: U32

  @ Guard g1
  guard g1

  @ Guard g2
  guard g2: U32

  @ Signal s1
  signal s1: U32

  @ Signal s2
  signal s2

  @ Signal s3
  signal s3

  @ Signal s4
  signal s4

  @ Signal s5
  signal s5

  @ Signal s6
  signal s6

  @ Initial transition
  initial do { a1 } enter C

  @ Choice C
  choice C { if g1 enter S1 else enter S2 }

  @ State S1
  state S1

  @ State S2
  state S2 {

    entry do { a1, a2 }
    exit do { a1, a2 }

    @ Initial transition
    initial do { a1, a2 } enter S3

    @ Choice C
    choice C { if g1 do { a1, a2 } enter S1 else do { a2, a3 } enter S2.S3 }

    @ State S3
    state S3

    @ Transition to S1
    on s1 if g1 do { a1 } enter C
    on s2 if g1 enter S1

    @ Transition to S1
    on s3 if g1 enter S1

    @ Transition to S1
    on s4 enter S1

    @ Internal transition
    on s5 if g1 do { a1 }

    @ Internal transition
    on s6 do { a1 }

  }

}
