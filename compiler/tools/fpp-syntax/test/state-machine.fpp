module M {

    active component C { 
        state machine S {
            initial enter IDLE
            state IDLE {
                initial enter RUNNING
                state RUNNING
            }
        } 
        state machine S1
    }
}

