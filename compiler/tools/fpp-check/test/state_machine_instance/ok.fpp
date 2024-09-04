module M {
  state machine S
}

state machine T

active component C {

  state machine S

  state machine instance s1: M.S priority 1 block
  state machine instance s2: T priority 2 drop
  state machine instance s3: S priority 3 assert
  state machine instance s4: S priority 3+1 block 
  state machine instance s5: S priority 3+2 
  state machine instance s6: S 

}
