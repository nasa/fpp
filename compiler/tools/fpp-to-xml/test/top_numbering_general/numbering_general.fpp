module M {

  constant numPorts = 10

  port P

  passive component Source {
    output port pOut: [numPorts] P
  }

  passive component Target {
    sync input port pIn: P
  }

  instance source: Source base id 0x100
  instance target: Target base id 0x200

  topology T {

    instance source
    instance target

    connections C {

      source.pOut -> target.pIn
      source.pOut -> target.pIn
      source.pOut -> target.pIn

    }

  }

}
