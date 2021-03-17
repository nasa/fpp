passive component C {

}

instance c1: C base id 0x100
instance c2: C base id 0x200

topology T {

  private instance c1
  instance c2

}
