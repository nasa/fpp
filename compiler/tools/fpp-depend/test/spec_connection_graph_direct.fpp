locate instance a at "a.fpp"
locate constant b at "b.fpp"
locate instance c at "c.fpp"
locate constant d at "d.fpp"

topology T {

  connections C {

    a.out[b] -> c.in[d]

  }

}
