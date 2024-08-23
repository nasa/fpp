module Fw {
  port Log
  port LogText
  port Time
}

type T

active component C {

  time get port timeGetOut
  event port logOut
  text event port logTextOut

  event E(x: T) severity activity low format "{}"

}
