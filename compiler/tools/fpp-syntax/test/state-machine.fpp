module M {

    active component C { 
        state machine S {
            signal RTI
            action foobar
            initial enter IDLE
            state IDLE {
                initial enter RUNNING
                state RUNNING
            }
        } 
        state machine S1
    }
}

