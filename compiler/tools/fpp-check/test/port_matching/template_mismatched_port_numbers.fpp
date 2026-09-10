port P

interface I {
  sync input port pIn: [2] P
  output port pOut: [2] P
}

passive component CMatch {

  sync input port pIn: [2] P
  output port pOut: [2] P
  match pIn with pOut

}

passive component C2 {

  sync input port pIn: P
  output port pOut: P

}

instance cm: CMatch base id 0x100
instance c2: C2 base id 0x200

module template T(instance i: I) {

  topology Top {

    instance i
    instance c2

    connections P {
      i.pOut[0] -> c2.pIn
      c2.pOut -> i.pIn[1]
    }

  }

}

expand T(instance cm)
