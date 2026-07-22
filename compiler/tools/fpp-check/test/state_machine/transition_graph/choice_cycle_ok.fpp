state machine M {
    guard g1
    guard g2
    guard g3

    initial enter INIT

    signal entered
    action signalEntered

    state INIT {
        entry do { signalEntered }
        on entered enter C1

        choice C1 {
            if g1 enter C2 \
            else enter C3
        }
    }

    choice C2 {
        if g2 enter OTHER \
        else enter C3
    }

    choice C3 {
        if g3 enter FINAL \
        else enter OTHER
    }

    state FINAL
    state OTHER
}