locate port P at "P.fpp"
locate port Q at "Q.fpp"
locate constant a at "a.fpp"
locate constant b at "b.fpp"
locate constant c at "c.fpp"
locate constant d at "d.fpp"


interface J {
  async input port p: [a] P priority b
}

interface I {

    async input port q: [c] Q priority d

    import J

}

active component C {

  import I

}
