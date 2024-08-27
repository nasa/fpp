module Fw {
  port PrmGet
  port PrmSet
}

type T

active component Comp {

  param get port prmGetOut

  param set port prmSetOut

  param P: T

}
