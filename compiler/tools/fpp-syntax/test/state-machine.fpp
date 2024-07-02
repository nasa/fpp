module M {

    struct StateData {
        a: U32
        b: F32
    }

    active component C { 

        state machine S {
            signal RTI
            signal EV1: StateData
            action a1: StateData
            action a2
            guard g1: StateData
            guard g2
            initial enter IDLE
            junction j1 {if g1 do a1 enter SAFING \
                         else do a2 enter RUNNING}
            state IDLE {
                initial enter RUNNING
                on RTI do a1
                on RTI enter SAFING
                on RTI do a1 enter SAFING
                on RTI if g1 do a1 enter SAFING
                on RTI if g2 do a2 enter RUNNING
                state RUNNING
            }
             state SAFING {
                on RTI do a2
             }   
        }

        state machine S1
    }
}

