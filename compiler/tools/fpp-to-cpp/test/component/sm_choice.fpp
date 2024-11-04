module FppTest {

  active component SmChoiceActive {

    include "include/sm_choice.fppi"

  }

  queued component SmChoiceQueued {

    sync input port schedIn: Svc.Sched

    include "include/sm_choice.fppi"

  }

}
