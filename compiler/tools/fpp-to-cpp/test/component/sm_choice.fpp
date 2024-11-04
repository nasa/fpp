module FppTest {

  active component SmJunctionActive {

    include "include/sm_choice.fppi"

  }

  queued component SmJunctionQueued {

    sync input port schedIn: Svc.Sched

    include "include/sm_choice.fppi"

  }

}
