module template BodyLocT(constant p: U32) {
  locate constant bodyLocConst at "template_body_locate_dep.fpp"
  constant cOut = p + bodyLocConst
}

module M {
  expand BodyLocT(constant 1)
}
