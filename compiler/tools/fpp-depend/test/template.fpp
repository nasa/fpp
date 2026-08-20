locate interface ParamIface at "template_param_iface.fpp"
locate constant bodyConst at "template_body_dep.fpp"
locate type ArgType at "template_arg_type.fpp"
locate instance argInst at "template_arg_instance.fpp"

module template T(interface i: ParamIface, type ty) {
  constant cOut = bodyConst
  struct S { x: ty }
  topology Top {
    instance i
  }
}

module M {
  expand T(interface argInst, type ArgType)
}
