state machine M {@ Action a1
  action a1

  @ Action a2
  action a2

  include "subdir/state.fppi"

  state S2 {
    include "subdir/state.fppi"
  }
}
