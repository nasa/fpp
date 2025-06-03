@ A passive component
passive component PassiveTest {

  import DataProductSyncReceive
  import TypedPorts
  import SpecialPorts

  include "include/commands.fppi"
  include "include/events.fppi"
  include "include/params.fppi"
  include "include/external_params.fppi"
  include "include/products.fppi"
  include "include/telemetry.fppi"

}

@ A passive component with serial ports
passive component PassiveSerial {

  import TypedPorts
  import SerialPorts
  import SpecialPorts

  include "include/commands.fppi"
  include "include/events.fppi"
  include "include/telemetry.fppi"
  include "include/params.fppi"
  include "include/external_params.fppi"

}

@ A passive component with commands
passive component PassiveCommands {

  import TypedPorts
  import SpecialPorts

  include "include/commands.fppi"

}

@ A passive component with events
passive component PassiveEvents {

  import TypedPorts
  import SpecialPorts

  include "include/events.fppi"

}

@ A passive component with telemetry
passive component PassiveTelemetry {

  import TypedPorts
  import SpecialPorts

  include "include/telemetry.fppi"

}

@ A passive component with params
passive component PassiveParams {

  import TypedPorts
  import SpecialPorts

  include "include/params.fppi"

}

@ A passive component with external params only
passive component PassiveExternalParams {

  include "include/typed_ports.fppi"
  include "include/special_ports.fppi"

  include "include/external_params.fppi"

}

@ A passive component with sync data products
passive component PassiveSyncProducts {

  import DataProductSyncReceive
  import TypedPorts
  import SpecialPorts

  include "include/products.fppi"

}

@ A passive component with guarded data products
passive component PassiveGuardedProducts {

  import DataProductGuardedReceive
  import TypedPorts
  import SpecialPorts

  include "include/products.fppi"

}

@ A passive component with data product get
passive component PassiveGetProducts {

  import DataProductGet
  import TypedPorts
  import SpecialPorts

  include "include/products.fppi"

}

@ A passive component with product get and ports only
passive component PassiveGetProductPortsOnly {

  import DataProductGet

}

@ A passive component with sync product request and ports only
passive component PassiveSyncProductPortsOnly {

  import DataProductSyncReceive

}
