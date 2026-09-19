port P

module Lib {
  module template Tmpl(constant n: U32) {
    passive component GenComp {
      sync input port pIn: P
    }
    array GenArr = [n] U32
  }
}

module Expanded {
  expand Lib.Tmpl(constant 3)
}
