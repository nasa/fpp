locate type T at "T.fpp"
locate constant a at "a.fpp"
locate constant b at "b.fpp"

active component C {

  event E(x: T) severity activity low id a format "x={}" throttle b

}
