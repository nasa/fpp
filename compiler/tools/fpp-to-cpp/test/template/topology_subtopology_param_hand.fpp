port P

@ The interface required of the template parameter
interface I {
  output port p: P
}

passive component CO {
  output port p: P
}

passive component CI {
  sync input port p: P
}

instance cOut: CO base id 0x100
instance cIn: CI base id 0x200

@ The subtopology that is bound to the template parameter
topology Sub {
  instance cOut
  port p = cOut.p
}

module M {

  @ Deployment topology that imports a subtopology through a template parameter
  deployment topology SubParam {
    import Sub
    instance cIn
    connections C {
      Sub.p -> cIn.p
    }
  }

}
