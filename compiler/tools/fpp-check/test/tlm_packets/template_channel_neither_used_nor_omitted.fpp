interface I {}

module template T(instance i: I) {

  deployment topology Top {

    instance i

    telemetry packets P {

      packet P group 0 {

      }

    }

  }

}

expand T(instance c1)
