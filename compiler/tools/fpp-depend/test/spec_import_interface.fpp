locate port P at "P.fpp"
locate constant a at "a.fpp"
locate constant b at "b.fpp"

interface I {

    async input port p: [a] P priority b

}

active component C {

  import I

}
