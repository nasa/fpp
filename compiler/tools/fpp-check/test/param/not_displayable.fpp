module Fw {
  port PrmGet
  port PrmSet
}

type A

active component Comp {

  param get port prmGetOut

  param set port prmSetOut

  param P: A

}
