passive component C {

}

instance c: C base id 0x100

topology T1 {

  instance c

}

topology T2 {

  instance T1

}
