module M {

  @ An active component
  active component ActiveTest {

    include "include/internal_ports.fppi"
    include "include/product_ports_async.fppi"
    include "include/special_ports.fppi"
    include "include/typed_ports.fppi"
    include "include/typed_ports_async.fppi"

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
  include "include/special_ports.fppi"
  include "include/overflow_commands.fppi"
  include "include/overflow_typed_ports.fppi"
  include "include/overflow_product_ports.fppi"
  include "include/overflow_serial_ports.fppi"
  include "include/overflow_internal_ports.fppi"
}

@ An active component with serial ports
active component ActiveSerial {

  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"
  include "include/serial_ports.fppi"
  include "include/serial_ports_async.fppi"
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

  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/commands.fppi"
  include "include/commands_async.fppi"

}

@ An active component with events
active component ActiveEvents {

  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/events.fppi"

}

@ An active component with telemetry
active component ActiveTelemetry {

  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/telemetry.fppi"

}

@ An active component with params
active component ActiveParams {

  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/params.fppi"

}

@ An active component with async data products
active component ActiveAsyncProducts {

  include "include/product_ports_async.fppi"
  include "include/special_ports.fppi"
  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"

  include "include/products.fppi"

}

@ An active component with sync data products
active component ActiveSyncProducts {

  include "include/product_ports_sync.fppi"
  include "include/special_ports.fppi"
  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"

  include "include/products.fppi"

}

@ An active component with guarded data products
active component ActiveGuardedProducts {

  include "include/product_ports_guarded.fppi"
  include "include/special_ports.fppi"
  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"

  include "include/products.fppi"

}

@ An active component with data product get
active component ActiveGetProducts {

  include "include/product_ports_get.fppi"
  include "include/special_ports.fppi"
  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"

  include "include/products.fppi"

}

@ An active component with async product request and ports only
active component ActiveAsyncProductPortsOnly {

  include "include/product_ports_async.fppi"

}

@ An active component with only ports without arguments
active component ActiveNoArgsPortsOnly {

  include "include/typed_ports_no_args.fppi"

}

module M {

  @ An active component with state machines
  active component ActiveStateMachines {

    include "include/state_machines.fppi"

  }

}
