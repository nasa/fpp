module FppTest {

  active component SmJunctionActive {

    include "include/sm_junction.fppi"

  }

  queued component SmJunctionQueued {

    sync input port schedIn: Svc.Sched

    include "include/sm_junction.fppi"

  }

}
