module M {

    active component C { 

        state machine S {
            signal RTI
            action a1
            action a2
            guard g1
            initial enter IDLE
            state IDLE {
                initial enter RUNNING
                on RTI do a1
                on RTI enter SAFING
                on RTI do a1 enter SAFING
                on RTI if g1 do a1 enter SAFING
                state RUNNING
            }
             state SAFING {
                on RTI do a2
             }   
        }

        state machine S1
    }
}

