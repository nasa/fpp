@ A queued component
queued component QueuedTest {

  import DataProductAsyncReceive
  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts
  include "include/internal_ports.fppi"

  include "include/commands.fppi"
  include "include/commands_async.fppi"
  include "include/events.fppi"
  include "include/products.fppi"
  include "include/telemetry.fppi"
  include "include/params.fppi"

}

@ An active component with overflow behavior
queued component QueuedOverflow {
  import SpecialPorts
  include "include/overflow_commands.fppi"
  import TypedPortsOverflow
  import DataProductAsyncReceiveOverflow
  import SerialPortsOverflow
  include "include/overflow_internal_ports.fppi"
}

@ A queued component with serial ports
queued component QueuedSerial {

  import TypedPorts
  import TypedPortsAsync
  import SerialPorts
  import SerialPortsAsync
  import SpecialPorts
  include "include/internal_ports.fppi"

  include "include/commands.fppi"
  include "include/commands_async.fppi"
  include "include/events.fppi"
  include "include/telemetry.fppi"
  include "include/params.fppi"

}

@ An queued component with commands
queued component QueuedCommands {

  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/commands.fppi"
  include "include/commands_async.fppi"

}

@ An queued component with events
queued component QueuedEvents {

  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/events.fppi"

}

@ An queued component with telemetry
queued component QueuedTelemetry {

  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/telemetry.fppi"

}

@ An queued component with params
queued component QueuedParams {

  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/params.fppi"

}

@ A queued component with async data products
active component QueuedAsyncProducts {

  import DataProductAsyncReceive
  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/products.fppi"

}

@ A queued component with sync data products
active component QueuedSyncProducts {

  import DataProductSyncReceive
  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/products.fppi"

}

@ A queued component with guarded data products
active component QueuedGuardedProducts {

  import DataProductGuardedReceive
  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/products.fppi"

}

@ A queued component with data product get
active component QueuedGetProducts {

  import DataProductGet
  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/products.fppi"

}

@ An queued component with async product request and ports only
queued component QueuedAsyncProductPortsOnly {

  import DataProductAsyncReceive

}

@ An queued component with only ports without arguments
queued component QueuedNoArgsPortsOnly {

  import TypedPortsNoArgs

}
