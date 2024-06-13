active component C {
    async input port run: Svc.Sched
    state machine LedSm
    state machine instance led1: LedSm
}
