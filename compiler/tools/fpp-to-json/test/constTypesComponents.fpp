@ A switch with on and off state
active component Switch {

  # ----------------------------------------------------------------------
  # Types
  # ----------------------------------------------------------------------

  @ The state enumeration
  enum State {
    OFF @< The off state
    ON @< The on state
  }

  # ----------------------------------------------------------------------
  # Ports
  # ----------------------------------------------------------------------

  @ Command registration
  command reg port cmdRegOut

  @ Command input
  command recv port cmdIn

  @ Command response
  command resp port cmdResponseOut

  # ----------------------------------------------------------------------
  # Commands
  # ----------------------------------------------------------------------

  @ Set the state
  async command SET_STATE(
    $state: State @< The new state
  )

}

module Fw {
  port Cmd
  port CmdReg
  port CmdResponse
}
