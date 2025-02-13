topology T {

  instance c1

  telemetry packets P {

    packet P group 0 {
      c1.T
    }

    packet P group 1 {
      c1.T
    }

  }

}
