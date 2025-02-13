locate constant a at "a.fpp"
locate constant b at "b.fpp"
locate instance i at "i.fpp"

topology T {

  instance i

  telemetry packets P {

    packet P1 id a group b {
      i.c
    }

  }

}
