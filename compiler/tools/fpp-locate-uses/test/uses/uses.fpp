array A_use = [3] A
constant a_use = a
array T_use = [3] T
array S_use = [3] S
constant E_use = E.X

module M {
  array A_use = [3] A
  constant a_use = a
  array T_use = [3] T
  array S_use = [3] S
  constant E_use = E.X
}

passive component C2 {
  sync input port P_use: P
  array C1_A_use = [3] C1.A
  constant C1_a_use = C1.a
  array C1_S_use = [3] C1.S
  constant C1_E_use = C1.E.X
}
