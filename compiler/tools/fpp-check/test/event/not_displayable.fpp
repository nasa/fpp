module Fw {
  
  port Log
  port LogText

}

type A

active component C {

  event E(x: A) \
    severity activity low \
    format ""

  event port logOut

  text event port logTextOut

}
