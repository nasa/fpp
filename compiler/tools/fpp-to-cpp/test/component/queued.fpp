@ A queued component
queued component Queued {

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

@ An queued component with commands
queued component QueuedCommands {

  include "include/general_ports.fppi"
  include "include/general_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/commands.fppi"
  include "include/commands_async.fppi"

}

@ An queued component with events
queued component QueuedEvents {

  include "include/general_ports.fppi"
  include "include/general_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/events.fppi"

}

@ An queued component with telemetry
queued component QueuedTelemetry {

  include "include/general_ports.fppi"
  include "include/general_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/telemetry.fppi"

}

@ An queued component with params
queued component QueuedParams {

  include "include/general_ports.fppi"
  include "include/general_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/params.fppi"

}
