port P

interface I {
  sync input port pIn: [2] P
  output port pOut: [2] P
}

passive component CArg {

  sync input port pIn: [2] P
  output port pOut: [2] P

}

passive component CMatch {

  sync input port pIn: [2] P
  output port pOut: [2] P
  match pIn with pOut

}

instance ca: CArg base id 0x100
instance cm: CMatch base id 0x200

module template T(instance i: I) {

  topology Top {

    instance i
    instance cm

    connections P {
      cm.pOut -> i.pIn
      i.pOut -> cm.pIn
    }

  }

}

expand T(instance ca)
