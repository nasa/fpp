topology T {

  instance c1
  instance c2

  telemetry packets P {

    packet P1 id 0 group 0 {
      c1.T
    }

    packet P2 id 0 group 1 {
      c2.T
    }

  }

}
