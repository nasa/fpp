passive component Events {

  event port eventOut

  text event port textEventOut

  time get port timeGetOut

  event E1 severity activity low format "E1"
  event E2 severity activity low id 10 format "E2"
  event E3(a: U32) severity activity low format "E3: a={}"
  event E4(a: U32) severity activity low format "E4: a={}" throttle 10

}
