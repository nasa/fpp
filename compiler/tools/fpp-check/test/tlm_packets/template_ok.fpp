# A packet set may name the channels of the instances that an imported
# topology from a template contributes through its instance parameters

interface I {}

module template T(instance i: I, instance j: I) {

  topology Sub {

    instance i
    instance j

  }

}

expand T(instance c1, instance c2)

deployment topology Top {

  import Sub

  telemetry packets P {

    packet P group 0 {
      c1.T
    }

  } omit {
    c2.T
  }

}
