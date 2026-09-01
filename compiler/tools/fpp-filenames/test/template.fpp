port P

passive component TopComp {
  sync input port pIn: P
}

module template T(constant n: U32) {
  array TemplateArr = [n] U32
  passive component TemplateComp {
    sync input port pIn: P
  }
}

module M {
  expand T(constant 3)
}
