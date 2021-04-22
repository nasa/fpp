module M {

  constant numPorts = 10

  port P

  passive component Source {
    sync input port pIn: [numPorts] P
    output port pOut: [numPorts] P
    match pIn with pOut
  }

  passive component Target {
    sync input port pIn: [numPorts] P
    output port pOut: [numPorts] P
  }

  instance source: Source base id 0x100
  instance target1: Target base id 0x200
  instance target2: Target base id 0x300
  instance target3: Target base id 0x400

  topology T {

    instance source
    instance target1
    instance target2
    instance target3

    connections C {

      source.pOut[1] -> target1.pIn
      target1.pOut -> source.pIn

      source.pOut -> target2.pIn
      target2.pOut -> source.pIn[2]

      source.pOut -> target3.pIn
      target3.pOut -> source.pIn

    }

  }

}
