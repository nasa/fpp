@ A passive component
passive component PassiveTest {

  include "include/product_ports_sync.fppi"
  include "include/typed_ports.fppi"
  include "include/special_ports.fppi"

  include "include/commands.fppi"
  include "include/events.fppi"
  include "include/params.fppi"
  include "include/products.fppi"
  include "include/telemetry.fppi"

}

@ A passive component with serial ports
passive component PassiveSerial {

  include "include/typed_ports.fppi"
  include "include/serial_ports.fppi"
  include "include/special_ports.fppi"

  include "include/commands.fppi"
  include "include/events.fppi"
  include "include/telemetry.fppi"
  include "include/params.fppi"

}

@ A passive component with commands
passive component PassiveCommands {

  include "include/typed_ports.fppi"
  include "include/special_ports.fppi"

  include "include/commands.fppi"

}

@ A passive component with events
passive component PassiveEvents {

  include "include/typed_ports.fppi"
  include "include/special_ports.fppi"

  include "include/events.fppi"

}

@ A passive component with telemetry
passive component PassiveTelemetry {

  include "include/typed_ports.fppi"
  include "include/special_ports.fppi"

  include "include/telemetry.fppi"

}

@ A passive component with params
passive component PassiveParams {

  include "include/typed_ports.fppi"
  include "include/special_ports.fppi"

  include "include/params.fppi"

}

@ A passive component with sync data products
passive component PassiveSyncProducts {

  include "include/product_ports_sync.fppi"
  include "include/special_ports.fppi"
  include "include/typed_ports.fppi"

  include "include/products.fppi"

}

@ A passive component with guarded data products
passive component PassiveGuardedProducts {

  include "include/product_ports_guarded.fppi"
  include "include/special_ports.fppi"
  include "include/typed_ports.fppi"

  include "include/products.fppi"

}

@ A passive component with data product get
passive component PassiveGetProducts {

  include "include/product_ports_get.fppi"
  include "include/special_ports.fppi"
  include "include/typed_ports.fppi"

  include "include/products.fppi"

}

@ A passive component with product get and ports only
passive component PassiveGetProductPortsOnly {

  include "include/product_ports_get.fppi"

}

@ A passive component with sync product request and ports only
passive component PassiveSyncProductPortsOnly {

  include "include/product_ports_sync.fppi"

}
