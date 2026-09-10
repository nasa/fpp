port P

passive component TopComp {
  sync input port pIn: P
}

module M {
  array TemplateArr = [3] U32
  passive component TemplateComp {
    sync input port pIn: P
  }
}
