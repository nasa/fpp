@ A queued component
queued component QueuedTest {

  include "include/product_ports_async.fppi"
  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"
  include "include/special_ports.fppi"
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
  include "include/special_ports.fppi"
  include "include/overflow_commands.fppi"
  include "include/overflow_typed_ports.fppi"
  include "include/overflow_product_ports.fppi"
  include "include/overflow_serial_ports.fppi"
  include "include/overflow_internal_ports.fppi"
}

@ A queued component with serial ports
queued component QueuedSerial {

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

@ An queued component with commands
queued component QueuedCommands {

  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/commands.fppi"
  include "include/commands_async.fppi"

}

@ An queued component with events
queued component QueuedEvents {

  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/events.fppi"

}

@ An queued component with telemetry
queued component QueuedTelemetry {

  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/telemetry.fppi"

}

@ An queued component with params
queued component QueuedParams {

  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"
  include "include/special_ports.fppi"

  include "include/params.fppi"

}

@ A queued component with async data products
active component QueuedAsyncProducts {

  include "include/product_ports_async.fppi"
  include "include/special_ports.fppi"
  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"

  include "include/products.fppi"

}

@ A queued component with sync data products
active component QueuedSyncProducts {

  include "include/product_ports_sync.fppi"
  include "include/special_ports.fppi"
  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"

  include "include/products.fppi"

}

@ A queued component with guarded data products
active component QueuedGuardedProducts {

  include "include/product_ports_guarded.fppi"
  include "include/special_ports.fppi"
  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"

  include "include/products.fppi"

}

@ A queued component with data product get
active component QueuedGetProducts {

  include "include/product_ports_get.fppi"
  include "include/special_ports.fppi"
  include "include/typed_ports.fppi"
  include "include/typed_ports_async.fppi"

  include "include/products.fppi"

}

@ An queued component with async product request and ports only
queued component QueuedAsyncProductPortsOnly {

  include "include/product_ports_async.fppi"

}

@ An queued component with only ports without arguments
queued component QueuedNoArgsPortsOnly {

  include "include/typed_ports_no_args.fppi"

}
