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

module template T(instance i: I) {

  @ Subtopology from a template, wired through the instance parameter
  topology Sub {
    instance i
    instance cIn
    connections C {
      i.pOut -> cIn.pIn
    }
  }

}

module M {

  expand T(instance cOut)

  @ Deployment topology importing the subtopology from the template
  deployment topology InstParam {
    import Sub
  }

}
