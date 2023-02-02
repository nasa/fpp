@ A passive component
passive component Passive {

  include "include/general_ports.fppi"
  include "include/special_ports.fppi"

  include "include/commands.fppi"
  include "include/events.fppi"
  include "include/telemetry.fppi"
  include "include/params.fppi"

}

@ An passive component with commands
passive component PassiveCommands {

  include "include/general_ports.fppi"
  include "include/special_ports.fppi"

  include "include/commands.fppi"

}

@ An passive component with events
passive component PassiveEvents {

  include "include/general_ports.fppi"
  include "include/special_ports.fppi"

  include "include/events.fppi"

}

@ An passive component with telemetry
passive component PassiveTelemetry {

  include "include/general_ports.fppi"
  include "include/special_ports.fppi"

  include "include/telemetry.fppi"

}

@ An passive component with params
passive component PassiveParams {

  include "include/general_ports.fppi"
  include "include/special_ports.fppi"

  include "include/params.fppi"

}
