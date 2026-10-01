port P

passive component Producer {
  output port pOut: P
  output port pOut2: P
}

passive component Consumer {
  sync input port pIn: P
  sync input port pIn2: P
}

instance prod: Producer base id 0x100
instance cons: Consumer base id 0x200

interface I {
  output port pOut: P
}

module template T(instance i: I) {
  topology Outer {
    instance i
    instance cons
    connections C {
      i.pOut -> cons.pIn
    }
  }
}

expand T(instance Sub)

topology Sub {
  instance prod
  port pOut = prod.pOut
}
