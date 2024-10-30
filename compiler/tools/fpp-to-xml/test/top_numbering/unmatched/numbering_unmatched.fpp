module M {

  constant numPorts = 4

  port P

  passive component Source {

    output port pOut: [numPorts] P
    sync input port pIn: [numPorts] P

    match pOut with pIn

  }

  passive component Target {

    output port pOut: [numPorts] P
    sync input port pIn: [numPorts] P

  }

  instance source1: Source base id 0x0100
  instance target11: Target base id 0x1100

  instance source2: Source base id 0x0200
  instance target21: Target base id 0x2100

  instance source3: Source base id 0x300
  instance target31: Source base id 0x3100
  instance target32: Source base id 0x3200

  instance source4: Source base id 0x400
  instance target41: Source base id 0x4100
  instance target42: Source base id 0x4200

  topology T {

    # Case 1: Matching connections to the same target

    instance source1
    instance target11

    connections Case1 {
      unmatched source1.pOut[0] -> target11.pIn[0]
      unmatched target11.pOut[0] -> source1.pIn[0]
      unmatched source1.pOut[1] -> target11.pIn[1]
      unmatched target11.pOut[1] -> source1.pIn[1]
    }

    # Case 2: Non-matching connections to the same target

    instance source2
    instance target21

    connections Case2 {
      unmatched source2.pOut[0] -> target21.pIn[0]
      unmatched target21.pOut[0] -> source2.pIn[0]
      unmatched source2.pOut[1] -> target21.pIn[1]
    }

    # Case 3: Matching connections to different targets

    instance source3
    instance target31
    instance target32

    connections Case3 {
      unmatched source3.pOut[0] -> target31.pIn[0]
      unmatched target32.pOut[0] -> source3.pIn[0]
    }

    # Case 4: Non-matching connections to the same target
      
    instance source4
    instance target41
    instance target42

    connections Case4 {
      unmatched source4.pOut[0] -> target41.pIn[0]
      unmatched target42.pOut[1] -> source4.pIn[1]
    }

  }

}
