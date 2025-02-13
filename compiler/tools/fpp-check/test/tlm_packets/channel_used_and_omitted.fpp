topology T {

  instance c1

  telemetry packets P {

    packet P level 0 {
      c1.T
    }

  } omit {
    c1.T
  }

}
