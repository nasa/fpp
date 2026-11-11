module T {

  passive component C {

  }

  instance c: C base id 0x100

  topology T {

    instance T.c

  }

}
