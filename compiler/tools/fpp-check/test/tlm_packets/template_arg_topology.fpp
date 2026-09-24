# A channel identifier must name a component instance. An instance parameter
# bound to a topology does not represent one, so naming its channel is an
# error.

interface I {}

# Sub satisfies the empty interface I, but it is a topology and not a
# component instance
topology Sub {
}

module template T(instance i: I) {

  deployment topology Top {

    instance i

    telemetry packets P {

      packet P group 0 {
        i.T
      }

    }

  }

}

expand T(instance Sub)
