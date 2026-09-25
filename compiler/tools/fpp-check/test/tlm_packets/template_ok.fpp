interface I {}

module template T(instance i: I, instance j: I) {

  deployment topology Top {

    instance i
    instance j

    telemetry packets P {

      packet P group 0 {
        c1.T
      }

    } omit {
      c2.T
    }

  }

}

expand T(instance c1, instance c2)
