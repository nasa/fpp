module FppTest {

  active component SmStateActive {

    include "include/sm_state.fppi"

  }

  queued component SmStateQueued {

    sync input port schedIn: Svc.Sched

    include "include/sm_state.fppi"

  }

}
