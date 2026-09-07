module M {

  passive component C {

  }

  instance c1: C base id 0x500

  deployment topology T {

    instance c1

    telemetry packets P {

      packet P1 group 0 {
        c1.T
      }

    }

  }

}
