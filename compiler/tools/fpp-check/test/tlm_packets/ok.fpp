topology T {

  instance c1
  instance c2
  instance c3

  telemetry packets P1 {

    packet P1 id 0 level 0 {
      c1.T
      c2.T
    }

    packet P2 level 1 {
      c1.T
      c1.T
    }

  } omit {
    c3.T
  }

  telemetry packets P2 {

    packet P1 id 0 level 0 {
      c1.T
      c2.T
      c3.T
    }

  }

}
