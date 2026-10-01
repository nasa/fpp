port P

@ The interface required of the template parameter
interface I {
  output port pOut: P
}

passive component COut {
  output port pOut: P
}

passive component CIn {
  sync input port pIn: P
}

instance cOut: COut base id 0x100
instance cIn: CIn base id 0x200

module M {

  @ Deployment topology from a template, wired through the instance parameter
  deployment topology InstParam {
    instance cOut
    instance cIn
    connections C {
      cOut.pOut -> cIn.pIn
    }
  }

}
