module M {

  port P

  passive component Source {
    output port pOut: [5] P
  }

  passive component Target {
    sync input port pIn: [2] P
  }

  instance source: Source base id 0x100
  instance target: Target base id 0x200

  topology T {

    instance source
    instance target

    connections C {

      source.pOut -> target.pIn
      source.pOut -> target.pIn
      source.pOut[1] -> target.pIn[1]
      source.pOut -> target.pIn
      source.pOut -> target.pIn

    }

  }

}
