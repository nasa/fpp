locate constant c at "constant_ok.fpp"
constant c = 0

locate constant M.c at "constant_ok.fpp"
module M {
  constant c = 0
}

locate constant C.c at "constant_ok.fpp"
passive component C {
  constant c = 0
}

