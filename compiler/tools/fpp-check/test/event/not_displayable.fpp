module Fw {
  port Log
  port LogText
  port Time
}

type A

active component C {

  time get port timeGetOut
  event port logOut
  text event port logTextOut

  event E(x: A) severity activity low format "{}"

}
