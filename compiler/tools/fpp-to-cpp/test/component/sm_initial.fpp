module FppTest {

  active component SmInitialActive {

    include "include/sm_initial.fppi"

  }

  queued component SmInitialQueued {

    sync input port schedIn: Svc.Sched

    include "include/sm_initial.fppi"

  }

}
