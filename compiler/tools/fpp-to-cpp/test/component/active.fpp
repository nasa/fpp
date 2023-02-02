@ An active component
active component Active {

  include "include/general_ports.fppi"
  include "include/general_ports_async.fppi"
  include "include/special_ports.fppi"
  include "include/internal_ports.fppi"

  include "include/commands.fppi"
  include "include/commands_async.fppi"
  include "include/events.fppi"
  include "include/telemetry.fppi"
  include "include/params.fppi"

}

@ An active component with commands
active component ActiveCommands {

  include "include/general_ports.fppi"
  include "include/general_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/commands.fppi"
  include "include/commands_async.fppi"

}

@ An active component with events
active component ActiveEvents {

  include "include/general_ports.fppi"
  include "include/general_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/events.fppi"

}

@ An active component with telemetry
active component ActiveTelemetry {

  include "include/general_ports.fppi"
  include "include/general_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/telemetry.fppi"

}

@ An active component with params
active component ActiveParams {

  include "include/general_ports.fppi"
  include "include/general_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/params.fppi"

}
