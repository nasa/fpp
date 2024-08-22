module Fw {

  port PrmGet
  port PrmSet

}

type A

active component Comp {

  param P: A

  param get port prmGetOut

  param set port prmSetOut

}
