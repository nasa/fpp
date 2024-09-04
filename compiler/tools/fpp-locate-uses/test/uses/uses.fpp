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
  array C1_T_use = [3] C1.T
  constant C1_E_use = C1.E.X
}

active component C3 {
  async input port P_use: P
  command recv port cmdIn
  command reg port cmdRegOut
  command resp port cmdRespOut
  event port eventOut
  param get port paramGetOut
  param set port paramSetOut
  telemetry port tlmOut
  text event port textEventOut
  time get port timeGetOut
  state machine instance S_use: S
  state machine instance M_S_use: M.S
  state machine instance C1_S_use: C1.S
  state machine instance M_C1_S_use: M.C1.S
}

active component C4 {
  product request port productRequestOut
  async product recv port productRecvIn priority product_recv_priority
  product send port productSendOut
  time get port timeGetOut
  product container C id container_id default priority container_priority
  product record R: RecordType id record_id
}

instance c12: C1 \
  base id base_id_def \
  queue size queue_size_def \
  stack size stack_size_def \
  priority priority_def \
  cpu cpu_def \
{
  phase Phases.setup "code"
}

topology T2 {
  import T1
  instance c11
}

module M {

  instance c12: C1 base id 0x200 {
    phase Phases.teardown "code"
  }

  topology T2 {
    import T1
    instance c11
  }

}
