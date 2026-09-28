# A packet set may name the channel of a component instance bound to an
# instance parameter, both in the packet list and in the omit list.
# Compare template_ok, which names the bound instances directly.

interface I {}

module template T(instance i: I, instance j: I) {

  deployment topology Top {

    instance i
    instance j

    telemetry packets P {

      packet P group 0 {
        i.T
      }

    } omit {
      j.T
    }

  }

}

expand T(instance c1, instance c2)
