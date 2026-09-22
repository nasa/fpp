# An incomplete model: Lib.Tmpl is not defined in any file passed to the tool
locate template Lib.Tmpl at "template_expand_incomplete_def.fppi"

module Local {
  module template Tmpl(constant n: U32) {
    array GenArr = [n] U32
  }
}

module Expanded {

  state machine SM {
    initial enter S
    state S
  }

  expand Local.Tmpl(constant 3)

  expand Lib.Tmpl(constant 3)

}
