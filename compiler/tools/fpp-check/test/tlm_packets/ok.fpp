topology T {

  instance c1
  instance c2
  instance c3

  telemetry packets P {

    packet P1 level 0 {
      c1.T
      c2.T
    }

    packet P2 level 1 {
      c1.T
    }

  } omit {
    c3.T
  }

}
