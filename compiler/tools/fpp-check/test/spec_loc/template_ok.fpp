locate template T at "template_ok.fpp"

module template T {
  constant a = 0
}

locate template M.T at "template_ok.fpp"

module M {
  module template T {
    constant a = 0
  }
}
