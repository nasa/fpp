# An include specifier in a module template body
module template IncT(constant p: U32) {
  include "template_include.fppi"
  constant cOut = p + includedConst
}

module M {
  expand IncT(constant 1)
}
