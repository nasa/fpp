locate type T at "T.fpp"
locate constant a at "a.fpp"
locate constant b at "b.fpp"
locate constant c at "c.fpp"

passive component C {

  telemetry T: T id a low { red b } high { red c }

}
