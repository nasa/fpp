module M {

  @ An active component
  active component ActiveTest {

    include "include/internal_ports.fppi"
    import DataProductAsyncReceive
    import SpecialPorts
    import TypedPorts
    import TypedPortsAsync

    include "include/commands.fppi"
    include "include/commands_async.fppi"
    include "include/events.fppi"
    include "include/params.fppi"
    include "include/products.fppi"
    include "include/telemetry.fppi"

  }

}

@ An active component with overflow behavior
active component ActiveOverflow {
  import SpecialPorts
  include "include/overflow_commands.fppi"
  import TypedPortsOverflow
  import DataProductAsyncReceiveOverflow
  import SerialPortsOverflow
  include "include/overflow_internal_ports.fppi"
}

@ An active component with serial ports
active component ActiveSerial {

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

@ An active component with commands
active component ActiveCommands {

  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/commands.fppi"
  include "include/commands_async.fppi"

}

@ An active component with events
active component ActiveEvents {

  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/events.fppi"

}

@ An active component with telemetry
active component ActiveTelemetry {

  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/telemetry.fppi"

}

@ An active component with params
active component ActiveParams {

  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/params.fppi"

}

@ An active component with async data products
active component ActiveAsyncProducts {

  import DataProductAsyncReceive
  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/products.fppi"

}

@ An active component with sync data products
active component ActiveSyncProducts {

  import DataProductSyncReceive
  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/products.fppi"

}

@ An active component with guarded data products
active component ActiveGuardedProducts {

  import DataProductGuardedReceive
  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/products.fppi"

}

@ An active component with data product get
active component ActiveGetProducts {

  import DataProductGet
  import TypedPorts
  import TypedPortsAsync
  import SpecialPorts

  include "include/products.fppi"

}

@ An active component with async product request and ports only
active component ActiveAsyncProductPortsOnly {

  import DataProductAsyncReceive

}

@ An active component with only ports without arguments
active component ActiveNoArgsPortsOnly {

  import TypedPortsNoArgs

}

module ExternalSm {

  @ An active component with external state machines
  active component ActiveExternalStateMachines {

    include "include/external_state_machines.fppi"

  }

}
