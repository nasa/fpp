module M {

  constant numPorts = 5

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

  instance source5: Source base id 0x500
  instance target51: Source base id 0x5100
  instance target52: Source base id 0x5200
  instance target53: Source base id 0x5300
  instance target54: Source base id 0x5400
  instance target55: Source base id 0x5500

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

    # Case 5: Mixed matched and unmatched connections

    instance source5
    instance target51
    instance target52
    instance target53
    instance target54
    instance target55

    connections Case5 {
      # Matched connections with explicit numbering
      source5.pOut[0] -> target51.pIn
      target51.pOut -> source5.pIn[0]
      # Matched connections with implicit numbering at pOut
      source5.pOut -> target52.pIn
      target52.pOut -> source5.pIn[1]
      # Matched connections with implicit numbering at pIn
      source5.pOut[2] -> target53.pIn
      target53.pOut -> source5.pIn
      # Matched connections with implicit numbering on both sides
      source5.pOut -> target54.pIn
      target54.pOut -> source5.pIn
      # Unmatched connection
      unmatched source5.pOut -> target55.pIn
    }

  }

}
